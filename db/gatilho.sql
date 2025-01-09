-- Procedimento armazenado que ativa o sino de notificações do canal creator
-- para o canal spectator
CREATE OR REPLACE PROCEDURE turnBellOn(spectator VARCHAR(256), creator VARCHAR(256))
BEGIN ATOMIC
    UPDATE subscription SET isbellon = true
    WHERE spectatorUserName = spectator AND creatorUserName = creator;
END;

DROP PROCEDURE turnbellon;

-- Função equivalente para implementação de gatilho
-- (O PostgreSQL não suporta o uso de procedimentos em gatilhos, apenas funcões.
--  Ver: https://www.postgresql.org/docs/17/sql-createtrigger.html)
CREATE OR REPLACE FUNCTION turnBellOn()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE subscription SET isbellon = true
    WHERE spectatorUserName = NEW.spectatorUserName
    AND creatorUserName = NEW.creatorUserName;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Gatilho que ativa o sino de notificações quando um canal curte uma publicação
-- de um canal que é inscrito
CREATE TRIGGER likeTurnsBellOn
AFTER UPDATE OF liked ON visualization
FOR EACH ROW
WHEN (NEW.liked = true)
EXECUTE FUNCTION turnBellOn();

