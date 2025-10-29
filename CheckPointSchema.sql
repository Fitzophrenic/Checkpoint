DROP SCHEMA IF EXISTS CheckPoint;
CREATE SCHEMA CheckPoint;
USE CheckPoint;


create table appUser (
	userID char(12) not NULL,
    username varchar(20) not NULL default 'big money baller',
    userJSON json,
    primary key(userID),
    UNIQUE(userID)

);

create table Project (
	projectID char(12) not NULL,
    projectName varchar(50) not NULL default 'untitlted project',
    OwnerID char(12) not NULL,
    projectJSON json,
    primary key(projectID),
	UNIQUE(projectID)
);


DELIMITER $$
 CREATE FUNCTION compileUserJSON(userIDvar char(12)) 
 returns JSON
 DETERMINISTIC
 BEGIN
    DECLARE endJSON JSON;
	DECLARE userProjectsJSON JSON;
    
    
    SELECT userJSON into userProjectsJSON
    FROM appUser AS u
    WHERE u.userID = userIDvar;
	
	SELECT JSON_OBJECT(
		'userName', u.username,
		'userID', u.userID,
        'projects', JSON_ARRAYAGG(
            JSON_OBJECT(
                'projectID', p.projectID,
                'projectName', p.projectName,
                'permissionLevel', perm.permissionLevel
            )
        )
	)
    INTO endJSON
	FROM appUser AS u
    JOIN JSON_TABLE(
		userProjectsJSON,
		'$.projects.projectID[*]' COLUMNS (id CHAR(12) PATH '$')
	) AS userProjects
    JOIN Project as p ON p.projectID = userProjects.id

	 JOIN JSON_TABLE(
        p.projectJSON,
        '$.users[*]'
        COLUMNS (
            userID CHAR(12) PATH '$.userID',
            permissionLevel CHAR(1) PATH '$.permissionLevel'
        )
    ) AS perm
        ON perm.userID = u.userID
    WHERE u.userID = userIDvar
    GROUP BY u.userID;

    RETURN endJSON;
END$$
 
CREATE PROCEDURE deleteProject(IN projectIDToDelete CHAR(12))
DETERMINISTIC
BEGIN

	DECLARE userIDvar varchar(12);
	DECLARE currentProjectID varchar(12);
	DECLARE projectData JSON;
	DECLARE userJSONData JSON;
	DECLARE i INT default 0;
	DECLARE j INT default 0;
	DECLARE usersWithAccess INT DEFAULT 0;
    DECLARE numberOfProjects INT DEFAULT 0;
    
    
    SELECT projectJSON
    INTO projectData
    FROM Project
    WHERE ProjectID = projectIDToDelete;
   IF projectData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Project not found — cannot delete.';
    END IF;
    
    
    SELECT JSON_LENGTH(projectData, '$.users') INTO usersWithAccess;
	
    
    all_users: LOOP
        IF i >= usersWithAccess OR usersWithAccess IS NULL THEN
            LEAVE all_users; 
		END IF;
		
        
        
        SELECT JSON_UNQUOTE(JSON_EXTRACT(projectData, CONCAT('$.users[', i, '].userID')))
		INTO userIDvar;
        SELECT userJSON INTO userJSONData
        FROM appUser as u
        WHERE u.userID = userIDvar;
        
        SELECT JSON_LENGTH(userJSONData, '$.projects') INTO numberOfProjects;
		
        SET j = 0;

        remove_obj: LOOP
			 IF j >= numberOfProjects OR numberOfProjects IS NULL THEN
                LEAVE remove_obj;
            END IF;

            SELECT JSON_UNQUOTE(JSON_EXTRACT(userJSONData, CONCAT('$.projects[', j, '].projectID')))
            INTO currentProjectID;

            IF currentProjectID = projectIDToDelete THEN
                SET userJSONData = JSON_REMOVE(userJSONData, CONCAT('$.projects[', j, ']'));
                UPDATE appUser SET userJSON = userJSONData WHERE userID = userIDvar;
                LEAVE remove_obj;
            END IF;
            
			SET j = j + 1;

        END LOOP remove_obj;
        
        SET i = i + 1;
        
    END LOOP all_users;

	DELETE FROM Project
	WHERE ProjectID = projectIDToDelete;

END$$

CREATE PROCEDURE removeUserFromProject(in projectIDvar CHAR(12), userIDvar CHAR(12))
deterministic
BEGIN
	DECLARE projectData JSON;
	DECLARE userJSONData JSON;
	DECLARE currentProjectID varchar(12);
	DECLARE currentUserID varchar(12);
	DECLARE i INT default 0;
	DECLARE numberOfProjects INT DEFAULT 0;
	DECLARE usersWithAccess INT DEFAULT 0;

    SELECT projectJSON INTO projectData
    FROM Project
    WHERE ProjectID = projectIDvar;
    
	SELECT userJSON INTO userJSONData
    FROM appUser
	WHERE userID = userIDvar;
    
    IF projectData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Project not found — cannot work on it.';
    END IF;
    
	SELECT JSON_LENGTH(userJSONData, '$.projects') INTO numberOfProjects;
	SELECT JSON_LENGTH(projectData, '$.users') INTO usersWithAccess;

	remove_obj: LOOP
		
        IF i >= numberOfProjects OR numberOfProjects IS NULL THEN
            LEAVE remove_obj; 
		END IF;
        
		SELECT JSON_UNQUOTE(JSON_EXTRACT(userJSONData, CONCAT('$.projects[', i, '].projectID')))
		INTO currentProjectID;

		IF currentProjectID = projectIDvar THEN
			SET userJSONData = JSON_REMOVE(userJSONData, CONCAT('$.projects[', i, ']'));
			UPDATE appUser SET userJSON = userJSONData WHERE userID = userIDvar;
			LEAVE remove_obj;
		END IF;
            
		SET i = i + 1;

	END LOOP remove_obj;
    
    SET i =0;
    
	remove_user: LOOP
		IF i >= usersWithAccess OR usersWithAccess IS NULL THEN
            LEAVE remove_user; 
		END IF;
        
        SELECT JSON_UNQUOTE(JSON_EXTRACT(userJSONData, CONCAT('$.users[', i, '].userID')))
		INTO currentUserID;

		IF currentUserID = userIDvar THEN
			SET projectData = JSON_REMOVE(userJSONData, CONCAT('$.users[', i, ']'));
			UPDATE project SET projectJSON = projectData WHERE projectID = projectIDvar;
			LEAVE remove_user;
		END IF;
            
		SET i = i + 1;
        
	END LOOP remove_user;

END$$

CREATE PROCEDURE addUserToProject(in projectIDvar CHAR(12), userIDvar CHAR(12), permLevel CHAR)
deterministic
BEGIN
	DECLARE projectData JSON;
	DECLARE userJSONData JSON;
	DECLARE projectNameIns VARCHAR(50);



     SELECT projectJSON, projectName 
    INTO projectData, projectNameIns
    FROM Project
    WHERE ProjectID = projectIDvar;
    
    IF projectData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Project not found — cannot work on it.';
    END IF;
    
	SELECT userJSON INTO userJSONData
    FROM appUser
	WHERE userID = userIDvar;
    
    IF userJSONData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'User not found — cannot update.';
    END IF;
    
    
    
    SET projectData = JSON_ARRAY_APPEND(
		projectData, 
		'$.users',
		JSON_OBJECT(
			'userID', userIDvar,
			'permissionLevel', permLevel
		)
	);
	UPDATE Project
    SET projectJSON = projectData
    WHERE ProjectID = projectIDvar;
    
	 SET userJSONData = JSON_ARRAY_APPEND(
		userJSONData, 
		'$.projects',
		JSON_OBJECT(
			'projectID', projectIDvar,
            'projectName', projectNameIns,
			'permissionLevel', permLevel
		)
	);
    UPDATE appUser
    SET userJSON = userJSONData
    WHERE userID = userIDvar;
    
END $$

DELIMITER ;

 INSERT INTO appUser VALUES 
('000000000001', 'Big dawg', 
'{
        "userID": "000000000001",
        "projects": [
            {
                "projectID": "000000000101",
                "projectName": "HamburgerHelper",
                "permissionLevel": "r"
            }
        ],
        "userName": "Big dawg"
    }'),
('000000000002', 'Lil Chef', 
'{
    "userID": "000000000002",
    "projects": [
        {
            "projectID": "000000000101",
            "projectName": "HamburgerHelper",
            "permissionLevel": "w"
        },
        {
            "projectID": "000000000102",
            "projectName": "PizzaPalace",
            "permissionLevel": "r"
        }
    ],
    "userName": "Lil Chef"
}'),
('000000000003', 'Burger Boss', 
'{
    "userID": "000000000003",
    "projects": [
        {
            "projectID": "000000000101",
            "projectName": "HamburgerHelper",
            "permissionLevel": "o"
        },
        {
            "projectID": "000000000103",
            "projectName": "SushiSpot",
            "permissionLevel": "r"
        }
    ],
    "userName": "Burger Boss"
}'),
('000000000004', 'Pizza Pro', 
'{
    "userID": "000000000004",
    "projects": [
        {
            "projectID": "000000000102",
            "projectName": "PizzaPalace",
            "permissionLevel": "r"
        }
    ],
    "userName": "Pizza Pro"
}');

INSERT INTO Project VALUES
('000000000101', 'HamburgerHelper', '000000000001', 
'{

	"projectName": "HamburgerHelper",
    "users": [
        {
            "userID": "000000000001",
            "permissionLevel": "r"
        },
        {
            "userID": "000000000002",
            "permissionLevel": "w"
        },
        {
            "userID": "000000000003",
            "permissionLevel": "o"
        }
    ]
  }');

select compileUserJSON('000000000001');

CALL removeUserFromProject('000000000101', '000000000002');
select projectJSON
from project
where projectID = '000000000101';

CALL addUserToProject('000000000101', '000000000004', 'r');
SELECT * from appUser;
SELECT * from Project;

