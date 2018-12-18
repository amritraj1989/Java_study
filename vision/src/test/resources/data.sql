	insert 
    into
        DRIVERS
        (DRV_ID, DGD_GRADE_CODE, DRIVER_FORENAME, DRIVER_MIDDLENAME, DRIVER_SURNAME, EA_ACCOUNT_CODE, EA_ACCOUNT_TYPE, EA_C_ID, NAT_INS_NO, TITLE, VERSIONTS) 
    values
        (1, 'U', 'Bob', 'E.', 'Smith', '00094067', 'C', 1, 'MAL_TESTING', 'Mr.', '1990-12-16 10:14:32.103010342');    
        
    insert 
    into
        DRIVERS
        (DRV_ID, DGD_GRADE_CODE, DRIVER_FORENAME, DRIVER_MIDDLENAME, DRIVER_SURNAME, EA_ACCOUNT_CODE, EA_ACCOUNT_TYPE, EA_C_ID, NAT_INS_NO, TITLE) 
    values
        (2, 'U', 'Craig', 'P', 'Jones', '00094067', 'C', 1, 'MAL_TESTING', 'Mr.');
        
    insert 
    into
        DRIVERS
        (DRV_ID, DGD_GRADE_CODE, DRIVER_FORENAME, DRIVER_MIDDLENAME, DRIVER_SURNAME, EA_ACCOUNT_CODE, EA_ACCOUNT_TYPE, EA_C_ID, NAT_INS_NO, TITLE) 
    values
        (3, 'U', 'Jim', null, 'Jones', '00094067', 'C', 1, 'MAL_TESTING', 'Mr.');
        
    insert 
    into
        DRIVERS
        (DRV_ID, DGD_GRADE_CODE, DRIVER_FORENAME, DRIVER_MIDDLENAME, DRIVER_SURNAME, EA_ACCOUNT_CODE, EA_ACCOUNT_TYPE, EA_C_ID, NAT_INS_NO, TITLE) 
    values
        (4, 'U', 'Bob', null, 'Barker', '00094067', 'C', 1, 'MAL_TESTING', 'Mr.');
        
    insert 
    into
        DRIVERS
        (DRV_ID, DGD_GRADE_CODE, DRIVER_FORENAME, DRIVER_MIDDLENAME, DRIVER_SURNAME, EA_ACCOUNT_CODE, EA_ACCOUNT_TYPE, EA_C_ID, NAT_INS_NO, TITLE) 
    values
        (5, 'U', 'Bev', null, 'Barker', '00094067', 'C', 1, 'MAL_TESTING', 'Mrs.');
        
    insert
    into
        DRIVERS
        (DRV_ID, DGD_GRADE_CODE, DRIVER_FORENAME, DRIVER_MIDDLENAME, DRIVER_SURNAME, EA_ACCOUNT_CODE, EA_ACCOUNT_TYPE, EA_C_ID, NAT_INS_NO, TITLE) 
    values
        (142851, 'U', 'Wayne', null, 'Sibley', '00094067', 'C', 1, 'MAL_TESTING', 'Mr.');

    insert
    into
        VSESSION
        (AUDSID, USERNAME) 
    values
        (1234567, 'Testing');