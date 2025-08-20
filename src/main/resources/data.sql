-- Sample Standard Sizes
INSERT INTO standard_sizes (width, height, depth, volume) VALUES
(50, 30, 20, 30),
(100, 60, 40, 240),
(75, 45, 30, 100),
(25, 20, 15, 7),
(120, 80, 50, 480)
ON CONFLICT DO NOTHING;