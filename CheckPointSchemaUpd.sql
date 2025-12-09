DROP database IF EXISTS CheckPoint_DataBase;
CREATE DATABASE CheckPoint_DataBase;
USE CheckPoint_DataBase;


create table appUser (
    username varchar(20) not NULL,
    userPassword varchar(30) not NULL,
    userJSON json,
    personalCalendar json,
    primary key(username)
);

create table Project (
	projectID char(12) not NULL,
    projectName varchar(50) not NULL default 'untitlted project',
    OwnerID char(20) not NULL,
    projectJSON json,
    boardsJSON json,
    projectCalendar json,
    primary key(projectID),
	UNIQUE(projectID)
);


DELIMITER $$
 CREATE FUNCTION compileUserJSON(usernameVar VARCHAR(20)) 
 returns JSON
 DETERMINISTIC
 BEGIN
    DECLARE endJSON JSON;
	DECLARE userProjectsJSON JSON;
    
    
    SELECT userJSON into userProjectsJSON
    FROM appUser AS u
    WHERE u.username = usernameVar;
	
	SELECT JSON_OBJECT(
        'userName', u.username,
        'projects', COALESCE(
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'projectID', p.projectID,
                    'projectName', p.projectName,
                    'permissionLevel', perm.permissionLevel
                )
            ), JSON_ARRAY()
        )
    )
    INTO endJSON
	FROM appUser AS u
    LEFT JOIN JSON_TABLE(
        u.userJSON,
        '$.projects[*]' COLUMNS (projectID VARCHAR(20) PATH '$.projectID')
    ) AS userProjects ON TRUE
    LEFT JOIN Project AS p ON p.projectID = userProjects.projectID

	 LEFT JOIN JSON_TABLE(
        p.projectJSON,
        '$.users[*]'
        COLUMNS (
            username CHAR(12) PATH '$.username',
            permissionLevel CHAR(1) PATH '$.permissionLevel'
        )
    ) AS perm
        ON perm.username = u.username
    WHERE u.username = usernameVar
    GROUP BY u.username;

    RETURN endJSON;
END$$
 
CREATE PROCEDURE deleteProject(IN projectIDToDelete VARCHAR(20))
DETERMINISTIC
BEGIN

	DECLARE usernamevar VARCHAR(20);
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
		
        
        
        SELECT JSON_UNQUOTE(JSON_EXTRACT(projectData, CONCAT('$.users[', i, '].username')))
		INTO usernamevar;
        SELECT userJSON INTO userJSONData
        FROM appUser as u
        WHERE u.username = usernamevar;
        
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
                UPDATE appUser SET userJSON = userJSONData WHERE username = usernamevar;
                LEAVE remove_obj;
            END IF;
            
			SET j = j + 1;

        END LOOP remove_obj;
        
        SET i = i + 1;
        
    END LOOP all_users;

	DELETE FROM Project
	WHERE ProjectID = projectIDToDelete;

END$$

CREATE PROCEDURE removeUserFromProject(in projectIDvar VARCHAR(20), usernamevar VARCHAR(20))
deterministic
BEGIN
	DECLARE projectData JSON;
	DECLARE userJSONData JSON;
	DECLARE currentProjectID varchar(12);
	DECLARE currentusername VARCHAR(20);
	DECLARE i INT default 0;
	DECLARE numberOfProjects INT DEFAULT 0;
	DECLARE usersWithAccess INT DEFAULT 0;

    SELECT projectJSON INTO projectData
    FROM Project
    WHERE ProjectID = projectIDvar;
    
	SELECT userJSON INTO userJSONData
    FROM appUser
	WHERE username = usernamevar;
    
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
			UPDATE appUser SET userJSON = userJSONData WHERE username = usernamevar;
			LEAVE remove_obj;
		END IF;
            
		SET i = i + 1;

	END LOOP remove_obj;
    
    SET i =0;
    
	remove_user: LOOP
		IF i >= usersWithAccess OR usersWithAccess IS NULL THEN
            LEAVE remove_user; 
		END IF;
        
        SELECT JSON_UNQUOTE(JSON_EXTRACT(userJSONData, CONCAT('$.users[', i, '].username')))
		INTO currentusername;

		IF currentusername = usernamevar THEN
			SET projectData = JSON_REMOVE(userJSONData, CONCAT('$.users[', i, ']'));
			UPDATE project SET projectJSON = projectData WHERE projectID = projectIDvar;
			LEAVE remove_user;
		END IF;
            
		SET i = i + 1;
        
	END LOOP remove_user;

END$$

CREATE PROCEDURE addUserToProject(in projectIDvar VARCHAR(20), usernamevar VARCHAR(20), permLevel CHAR(1))
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
	WHERE username = usernamevar;
    
    IF userJSONData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'User not found — cannot update.';
    END IF;
    
    
    IF JSON_CONTAINS(projectData, JSON_OBJECT('username', usernamevar), '$.users') = 0 THEN
		SET projectData = JSON_ARRAY_APPEND(
			projectData, 
			'$.users',
			JSON_OBJECT(
				'username', usernamevar,
				'permissionLevel', permLevel
			)
		);
        UPDATE Project
		SET projectJSON = projectData
		WHERE ProjectID = projectIDvar;
	END IF;
	
    IF JSON_CONTAINS(userJSONData, JSON_OBJECT('projectID', projectIDvar), '$.projects') = 0 THEN

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
		WHERE username = usernamevar;
	END IF;
END $$

CREATE PROCEDURE changePermissionLevel(in projectIDvar CHAR(12), usernamevar VARCHAR(20), permLevel CHAR(1))
deterministic
BEGIN
	DECLARE projectData JSON;
	DECLARE userJSONData JSON;
	DECLARE currentProjectID varchar(12);
	DECLARE currentusername varchar(12);
	DECLARE i INT default 0;
	DECLARE numberOfProjects INT DEFAULT 0;
	DECLARE usersWithAccess INT DEFAULT 0;
	DECLARE userInProject INT DEFAULT 0;
	SELECT projectJSON INTO projectData
    FROM Project
    WHERE ProjectID = projectIDvar;
    
    IF projectData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Project not found — cannot work on it.';
    END IF;
    
	SELECT userJSON INTO userJSONData
    FROM appUser
	WHERE username = usernamevar;
    
    IF userJSONData IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'User not found — cannot update.';
    END IF;
	
     SELECT 
        JSON_CONTAINS(
            projectData,
            JSON_OBJECT('username', usernamevar),
            '$.users'
        )
    INTO userInProject;

    IF userInProject = 0 THEN
        CALL addUserToProject(projectIDvar, usernamevar, permLevel);
        
        -- Reload JSON because addUserToProject modified it (NEW)
        SELECT projectJSON INTO projectData FROM Project WHERE ProjectID = projectIDvar;
        SELECT userJSON INTO userJSONData FROM appUser WHERE username = usernamevar;
    END IF;
    
    
    SELECT JSON_LENGTH(userJSONData, '$.projects') INTO numberOfProjects;
	SELECT JSON_LENGTH(projectData, '$.users') INTO usersWithAccess;
    
    change_userJSON_NewLevel: LOOP
		IF i >= numberOfProjects OR numberOfProjects IS NULL THEN
            LEAVE change_userJSON_NewLevel; 
		END IF;
        
        SELECT JSON_UNQUOTE(JSON_EXTRACT(userJSONData, CONCAT('$.projects[', i, '].projectID')))
		INTO currentProjectID;
        
        IF currentProjectID = projectIDvar THEN
			SET userJSONData = JSON_SET(
				userJSONData,
				CONCAT('$.projects[', i, '].permissionLevel'),
				permLevel
			);
			UPDATE appUser SET userJSON = userJSONData WHERE username = usernamevar;
			LEAVE change_userJSON_NewLevel;
		END IF;
        
        
        
        SET i = i + 1;
    
    END LOOP change_userJSON_NewLevel;
    
    
    SET i = 0;
    
    update_user_in_projectJSON: LOOP
		IF i >= usersWithAccess OR usersWithAccess IS NULL THEN
            LEAVE update_user_in_projectJSON; 
		END IF;
        
        SELECT JSON_UNQUOTE(JSON_EXTRACT(projectData, CONCAT('$.users[', i, '].username')))
		INTO currentusername;

		IF currentusername = usernamevar THEN
			SET projectData = JSON_SET(
				projectData,
				CONCAT('$.users[', i, '].permissionLevel'),
				permLevel
			);
			UPDATE project SET projectJSON = projectData WHERE projectID = projectIDvar;
			LEAVE update_user_in_projectJSON;
		END IF;
            
		SET i = i + 1;
        
	END LOOP update_user_in_projectJSON;
    
END$$


DELIMITER ;

describe project;

