INSERT
OR IGNORE INTO @blockTable@
(
    x,
    y,
    z,
    world
)
VALUES(
    ?,
    ?,
    ?,
    ?
);