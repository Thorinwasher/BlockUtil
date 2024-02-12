CREATE TABLE IF NOT EXISTS blockTable (
    x INTEGER,
    y INTEGER,
    z INTEGER,
    world VARCHAR(36),
    PRIMARY KEY (x,y,z,world)
);