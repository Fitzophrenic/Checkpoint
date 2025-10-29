DROP SCHEMA IF EXISTS CheckPoint;
CREATE SCHEMA CheckPoint;
USE CheckPoint;


create table User (
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
    FROM User AS u
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
	FROM User AS u
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
DELIMITER ;
 
DELIMITER $$

 
CREATE PROCEDURE deleteProject(IN projectIDToDelete CHAR(12))
DETERMINISTIC
BEGIN
	DECLARE userIDvar varchar(12);
	DECLARE currentProjectID varchar(12);
	DECLARE projectData JSON;
	DECLARE userJSONData JSON;
	DECLARE i INT default 0;
	DECLARE j INT default 0;

    SELECT projectJSON
    INTO projectData
    FROM Project
    WHERE ProjectID = projectIDToDelete;
    
    SELECT JSON_LENGTH(projectData, '$.users') as usersWithAcess;
	all_users: LOOP
		IF i >= usersWithAcess THEN
            LEAVE all_users; 
		END IF;
		
        SELECT JSON_EXTRACT(projectData, CONCAT('$.users[', i, '].userID'))
		INTO userIDvar;
        SELECT userJSON INTO userJSONData
        FROM User as u
        WHERE u.userID = userIDvar;
        
        SELECT JSON_LENGTH(userJSON, '$.projects.projectID') as numberOfProjects;
        
        remove_obj: LOOP
			 IF j >= numberOfProjects THEN
                LEAVE remove_obj;
            END IF;

            SELECT JSON_UNQUOTE(JSON_EXTRACT(userJSONData, CONCAT('$.projects[', j, '].projectID')))
            INTO currentProjectID;

            IF currentProjectID = projectIDToDelete THEN
                SET userJSONData = JSON_REMOVE(userJSONData, CONCAT('$.projects[', j, ']'));
                UPDATE User SET userJSON = userJSONData WHERE userID = userIDvar;
                LEAVE remove_obj;
            END IF;
			SET j = j + 1;

        END LOOP remove_obj;
        
        SET i = i + 1;
        
    END LOOP all_users;

	DELETE from project as projdel
    where projdel.projectID = projectIDToDelete;
END$$
 
DELIMITER ;

 

