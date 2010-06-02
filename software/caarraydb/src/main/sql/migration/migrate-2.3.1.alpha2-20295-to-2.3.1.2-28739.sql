-- GF 28739 revert standard term source names

DELIMITER |

CREATE TRIGGER rename_standard_term_sources BEFORE UPDATE ON term_source FOR EACH ROW
BEGIN
IF OLD.name = 'MO' THEN SET NEW.name = 'MO'; END IF;
IF OLD.name = 'caArray' THEN SET NEW.name = 'caArray'; END IF;
END;
|

DELIMITER ;