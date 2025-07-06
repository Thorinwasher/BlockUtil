CREATE TABLE IF NOT EXISTS @blockTable@
(
    x
    INTEGER,
    y
    INTEGER,
    z
    INTEGER,
    world
    BINARY
(
    16
),
    PRIMARY KEY
(
    x,
    y,
    z,
    world
)
    );