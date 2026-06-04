package com.example.data.repository

data class MiniGame(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isPlayable: Boolean = false,
    val icon: String // Icon representation code (Material Icons string Name)
)

val GAMES_LIST = listOf(
    // 1. Puzzle Games (10 games)
    MiniGame("color_sort", "Color Sort Puzzle", "Sort colored liquids into matching tubes in this relaxing puzzle.", "Puzzle Games", true, "colorize"),
    MiniGame("maze_runner", "Maze Runner", "Find the shortest path out of 3D-simulated complex geometric grids.", "Puzzle Games", false, "grid_on"),
    MiniGame("block_fit", "Block Fit 3D", "Fit complex polyomino shapes into the designated board slot.", "Puzzle Games", false, "category"),
    MiniGame("word_search", "Word Search", "Locate all the hidden words dynamically buried in the letter grid.", "Puzzle Games", false, "search"),
    MiniGame("pipe_line", "Pipeline Plumber", "Connect water pipes of varying orientations to form a continuous line.", "Puzzle Games", false, "hardware"),
    MiniGame("tangle_master", "Tangle Untangler", "Untangle colorful knotted ropes in minimum moves.", "Puzzle Games", false, "hearing"),
    MiniGame("shadow_align", "Shadow Shape", "Rotate 3D objects to match their projected silhouettes perfectly.", "Puzzle Games", false, "contrast"),
    MiniGame("bridge_builder", "Bridge Blueprint", "Build structurally sound bridges to cross canyons using materials.", "Puzzle Games", false, "deck"),
    MiniGame("hex_puzzle", "Hexa Blocks", "Place colored hexagon tiles inside a honeycomb board configuration.", "Puzzle Games", false, "hive"),
    MiniGame("slide_blocks", "Slide Blocks Unblock", "Slide red wooden blocks through a crowded grid to exit.", "Puzzle Games", false, "swap_horiz"),

    // 2. Brain Games (10 games)
    MiniGame("sudoku", "Sudoku Pro", "Classic 9x9 logic grid containing missing digit completions.", "Brain Games", false, "dialpad"),
    MiniGame("math_flash", "Math Flash Master", "Resolve rapid-fire arithmetic inequalities with decreasing timers.", "Brain Games", false, "calculate"),
    MiniGame("hangman", "Hangman Wizard", "Guess the secret word letter-by-letter before run out of tries.", "Brain Games", false, "help_outline"),
    MiniGame("riddle_run", "Logic Riddle Trivia", "Teasing mind riddles that force out-of-the-box thinking.", "Brain Games", false, "psychology"),
    MiniGame("spatial_spin", "Spatial Rotate Quest", "Mentally rotate 3D geometries and detect matching structures.", "Brain Games", false, "rotate_right"),
    MiniGame("anagram_ace", "Anagram Solver", "Rearrange a wild jumble of letters to discover valid secret words.", "Brain Games", false, "shuffle"),
    MiniGame("iq_sequence", "Sequence Pattern", "Identify the missing element in challenging visual IQ series.", "Brain Games", false, "trending_up"),
    MiniGame("trivia_spark", "General Knowledge Spark", "Multiple-choice trivia questions from across science, art, and history.", "Brain Games", false, "lightbulb"),
    MiniGame("word_ladder", "Word Ladder", "Transform one word into another by altering a single letter at a step.", "Brain Games", false, "format_list_numbered"),
    MiniGame("cipher_crack", "Cipher Cryptography", "Decode messages with custom shifting substitutions and patterns.", "Brain Games", false, "lock_open"),

    // 3. Arcade Games (10 games)
    MiniGame("snake", "Retro Snake Classic", "Guide the greedy crawling hungry snake to munch apples and grow.", "Arcade Games", true, "pest_control_rodent"),
    MiniGame("flappy_bird", "Flappy Sky Jet", "Flap your wings or pilot a jet smoothly between tight columns.", "Arcade Games", false, "flight"),
    MiniGame("brick_breaker", "Brick Breaker Classic", "Bounce the fireball off your moving paddle to smash all bricks.", "Arcade Games", false, "grid_view"),
    MiniGame("stack_tower", "Stack Tower Deluxe", "Release sliding platform blocks exactly on top of each other.", "Arcade Games", false, "layers"),
    MiniGame("space_invaders", "Cosmic Invaders", "Defend your ship against advancing waves of pixelated alien craft.", "Arcade Games", false, "rocket_launch"),
    MiniGame("asteroid_dodge", "Asteroid Bullet Storm", "Navigate screen through falling debris while shooting obstacles.", "Arcade Games", false, "storm"),
    MiniGame("pac_dash", "Neon Maze Gobbler", "Navigate a retro neon corridor picking up dots while dodging ghosts.", "Arcade Games", false, "sports_motorsports"),
    MiniGame("frogger", "Traffic Crossing", "Help a frog cross busy highways and fast-moving rivers.", "Arcade Games", false, "directions_car"),
    MiniGame("dino_run", "Prehistoric Dino Jump", "Leap over cacti as a speed-running pixel dinosaur.", "Arcade Games", false, "pets"),
    MiniGame("ball_bounce", "Sling Bouncer", "Keep the neon bouncy ball in the air by creating safety shields.", "Arcade Games", false, "sports_tennis"),

    // 4. Reflex Games (10 games)
    MiniGame("reaction_timer", "Reaction Speed Test", "Tap as fast as humanly possible the millisecond screen colors shift.", "Reflex Games", true, "timer"),
    MiniGame("tap_challenge", "Super Tap Challenge", "Tap the rapidly appearing targets within a frantic 15-second rush.", "Reflex Games", true, "touch_app"),
    MiniGame("shulte_table", "Shulte Table Focus", "Tap numbers 1 to 25 spread across a random grid sequence.", "Reflex Games", false, "apps"),
    MiniGame("laser_dodge", "Laser Grid Evader", "Dodge sweeps of laser beams by triggering jumps in rhythm.", "Reflex Games", false, "bolt"),
    MiniGame("rhythm_tap", "Beat Beat Tap", "Tap falling musical symbols in perfect timing with the rhythm.", "Reflex Games", false, "audiotrack"),
    MiniGame("circle_rush", "Circle Orbit Tap", "Tap when the spinning cursor intercepts the target ring perfectly.", "Reflex Games", false, "adjust"),
    MiniGame("quick_draw", "Quick Draw Duel", "Tap instantly when the 'FIRE' banner flashes on the screen.", "Reflex Games", false, "gavel"),
    MiniGame("arrow_swipe", "Arrow Direction Swipe", "Swipe in the exact direction of the arrows, but opposite if blue!", "Reflex Games", false, "swap_calls"),
    MiniGame("catch_falling", "Catch Falling Stars", "Move a sliding basket left and right to catch golden stars.", "Reflex Games", false, "download"),
    MiniGame("color_match", "Color Swapper", "Tap to rotate a shield to block incoming projectiles matching the color.", "Reflex Games", false, "palette"),

    // 5. Memory Games (10 games)
    MiniGame("memory_match", "Memory Grid Match", "Match cards of hidden visual symbols within minimal tries.", "Memory Games", true, "extension"),
    MiniGame("simon_says", "Simon Says Rhythm", "Memorize and reproduce progressively lengthy audio-visual flashing sequences.", "Memory Games", true, "graphic_eq"),
    MiniGame("pattern_rebuild", "Pattern Grid Path", "View a pattern for 3 seconds then rebuild it on a clean grid.", "Memory Games", false, "view_quilt"),
    MiniGame("number_span", "Digit Span Recall", "A series of digits flashes briefly; type them back from memory.", "Memory Games", false, "password"),
    MiniGame("card_peek", "Card Deck Peek", "A layout of cards is shown; search for specific card indices quickly.", "Memory Games", false, "dashboard_customize"),
    MiniGame("face_recall", "Persona Name Recall", "Memorize portraits with their names, then map them back correctly.", "Memory Games", false, "face"),
    MiniGame("sound_memo", "Chime Sound Match", "Match identical chimes and tones based purely on hearing.", "Memory Games", false, "volume_up"),
    MiniGame("path_memo", "Lost Traveler Path", "Trace a complex path illuminated brief moments ago across a map.", "Memory Games", false, "insights"),
    MiniGame("shop_list", "Shopping List memory", "Remember a written group of groceries, then identify items in store.", "Memory Games", false, "shopping_cart"),
    MiniGame("pair_doubles", "Pair Twin Seekers", "Browse dynamic shapes and match matching identical twin objects.", "Memory Games", false, "dynamic_feed"),

    // 6. Strategy Games (10 games)
    MiniGame("tic_tac_toe", "Tic-Tac-Toe Smart", "Defeat the clever built-in AI in a classic grid combat.", "Strategy Games", true, "close"),
    MiniGame("minesweeper", "Minesweeper Vault", "Expose empty squares while flagging dangerous hidden mines.", "Strategy Games", true, "dangerous"),
    MiniGame("reversi", "Othello Reversi", "Sandwich your opponent's checkers to claim the board dominance.", "Strategy Games", false, "radio_button_checked"),
    MiniGame("hex_conquer", "Hex Territory Connect", "Draw pathways on hexagonal nodes to control the largest surface.", "Strategy Games", false, "grid_goldenratio"),
    MiniGame("checkers", "Drafts Checkers Classic", "Classic board strategy of jumping pieces and king conversion.", "Strategy Games", false, "grid_view"),
    MiniGame("chess_puzzle", "Mate in One", "Solve grandmaster chess scenarios by finding the single winning move.", "Strategy Games", false, "emoji_people"),
    MiniGame("battleship", "Submarine Hunter", "Target grid cells to locate and fire upon the hidden fleet.", "Strategy Games", false, "directions_boat"),
    MiniGame("tower_defense", "Castle Defense Mini", "Place arrow and ice towers strategic points to stop invaders.", "Strategy Games", false, "fort"),
    MiniGame("dots_boxes", "Dots & Boxes", "Join dots to draw boxes; claim the square when you close its fourth side.", "Strategy Games", false, "border_all"),
    MiniGame("risk_roll", "Troop Mobilize Roll", "Conquer neutral nodes by allocating dice rolls strategically.", "Strategy Games", false, "casino"),

    // 7. Physics Games (10 games)
    MiniGame("physics_balls", "Drop Gravity Balls", "Launch bouncing metal spheres to fall through a pegboard of values.", "Physics Games", false, "blur_circular"),
    MiniGame("gravity_connect", "Sling Gravity Hook", "Swing a satellite from star Orbit to Orbit using gravitational fields.", "Physics Games", false, "wifi_tethering"),
    MiniGame("balance_beam", "Balance Seesaw Scale", "Place weighted boxes to keep a tilting platform level.", "Physics Games", false, "scale"),
    MiniGame("ragdoll_fall", "Ragdoll Physics Jump", "Drop a dummy to descend safely through obstacle bars without impacts.", "Physics Games", false, "accessibility_new"),
    MiniGame("sand_flow", "Falling Sand Physics", "Direct continuous flow of falling sand grains into designated mugs.", "Physics Games", false, "grain"),
    MiniGame("bouncy_laser", "Laser Ray Reflector", "Place mirrors to bounce laser beams into core energy pods.", "Physics Games", false, "leak_add"),
    MiniGame("balloon_flight", "Wind Air Escape", "Blow air vectors using fans to guide a fragile balloon around spikes.", "Physics Games", false, "air"),
    MiniGame("marble_run", "Marble Slide Pipe", "Arrange slopes and tracks so a rolling marble reaches the finish hole.", "Physics Games", false, "toll"),
    MiniGame("water_fill", "Liquid Flow Tube", "Draw lines to guide flowing spring water to fill a dry bucket.", "Physics Games", false, "water"),
    MiniGame("magnet_pull", "Magnetic Ball Maze", "Utilize negative/positive poles to drag magnetic spheres to a goal.", "Physics Games", false, "explore"),

    // 8. Satisfying Games (10 games)
    MiniGame("bubble_pop", "Bubble Pop Relax", "Pop bubble wrap pockets for satisfying haptics, color splashes and pops.", "Satisfying Games", true, "blur_on"),
    MiniGame("slime_squish", "Slime Squeezer", "Squeeze colorful virtual slimes that deform under your fingers.", "Satisfying Games", false, "waves"),
    MiniGame("grass_cutter", "Lawn Mower Simulator", "Drive a tiny mower around a lush wild garden to trim it short.", "Satisfying Games", false, "grass"),
    MiniGame("wood_carving", "Chisel Sculptor", "Peel off shavings of a spinning wooden cylinder to sculpt shapes.", "Satisfying Games", false, "carpenter"),
    MiniGame("soap_slice", "ASMR Soap Slicer", "Slice fresh textured bars of soap into tiny satisfying cubes.", "Satisfying Games", false, "stacked_bar_chart"),
    MiniGame("organizer_pro", "Closet Organization", "Organize keys, cosmetics and pencils into their perfect custom boxes.", "Satisfying Games", false, "inventory"),
    MiniGame("glass_drop", "Glass Shatter Box", "Drop heavy glass ornaments on metallic tables with realistic pops.", "Satisfying Games", false, "broken_image"),
    MiniGame("zip_line", "Zipper Pull ASMR", "Slide zippers of various clothing objects up and down rapidly.", "Satisfying Games", false, "unfold_more"),
    MiniGame("power_wash", "High Pressure Clean", "Wash off dense thick black soot from virtual retro vehicles.", "Satisfying Games", false, "cleaning_services"),
    MiniGame("paint_roller", "Paint Roller Fill", "Paint plain concrete surfaces with a visual paint roller trail.", "Satisfying Games", false, "wallpaper"),

    // 9. Number Games (10 games)
    MiniGame("two_zero_four_eight", "2048 Master", "Swipe and merge tile cubes with matching digits to hit 2048.", "Number Games", true, "apps_outage"),
    MiniGame("number_merge", "Number Merge Link", "Connect matching adjacent digits in a line to merge and double.", "Number Games", false, "join_full"),
    MiniGame("ten_combine", "Make Ten Quest", "Select grid values that sum up to exactly ten to clear blocks.", "Number Games", false, "add_circle_outline"),
    MiniGame("prime_finder", "Prime Number Rush", "Identify and tap prime numbers in a flowing river of math.", "Number Games", false, "looks_one"),
    MiniGame("even_odd", "Even Odd Sorter", "Rapidly swipe even numbers to the left, odd numbers to the right.", "Number Games", false, "compare_arrows"),
    MiniGame("count_clicker", "Perfect Click Count", "Press keys according to a target rhythm without looking.", "Number Games", false, "plus_one"),
    MiniGame("math_crossword", "Math Cross Grid", "Fill cells with operators and values to make horizontal formulas correct.", "Number Games", false, "grid_goldenratio"),
    MiniGame("fibonacci_link", "Fibonacci Connect", "Connect numbers that follow the golden Fibonacci progression series.", "Number Games", false, "timeline"),
    MiniGame("grid_sum", "Sum Grid Cross", "Swipe lines that sum up to target header values in coordinates.", "Number Games", false, "border_inner"),
    MiniGame("digit_fall", "Digit Cascade Combo", "Drop falling numerical values to stack and synthesize bigger scores.", "Number Games", false, "arrow_downward"),

    // 10. Logic Games (10 games)
    MiniGame("logic_gates", "Circuit Logic Gates", "Toggle AND/OR/NOT switches to guide electricity to a target bulb.", "Logic Games", false, "electrical_services"),
    MiniGame("wolf_sheep", "River Side Ferry", "Carry wolf, sheep and lettuce across a river in a small boat legally.", "Logic Games", false, "waves"),
    MiniGame("tower_hanoi", "Towers of Hanoi", "Move wooden disks between poles while obeying size constraints.", "Logic Games", false, "density_large"),
    MiniGame("color_grid", "Flood Fill Logic", "Flood visual grids with single colors within constraints.", "Logic Games", false, "format_color_fill"),
    MiniGame("einstein_box", "Einstein Riddle Case", "Solve a grid of houses, pets and drinks using logical constraints.", "Logic Games", false, "question_mark"),
    MiniGame("nonogram", "Picross Nonogram", "Fill grids according to side listings to reveal pixel artwork.", "Logic Games", false, "crop_portrait"),
    MiniGame("water_jugs", "Three Jugs Fill", "Measure out exact liquid volumes utilizing 8, 5 and 3-liter canisters.", "Logic Games", false, "local_drink"),
    MiniGame("logic_grid", "Solve Clues Trivia", "Grid mapping interface where you cross off logic contradictions.", "Logic Games", false, "checklist"),
    MiniGame("switch_grid", "Toggle Switch Board", "Flip bulbs which toggle adjacent bulb states to turn all bulbs on.", "Logic Games", false, "toggle_on"),
    MiniGame("laser_split", "Laser Beam Splitter", "Place prisms to disperse single lights into secondary color rays.", "Logic Games", false, "filter_tilt_shift"),

    // 11. Extra Games to cross 100+ (11 additional games!)
    MiniGame("speed_sort", "Postal Cargo Sort", "Sort packages to matching belts before the sorter clogs up.", "Arcade Games", false, "local_post_office"),
    MiniGame("tile_slide", "Fifteen Jumble Slide", "Slide tiles 1 to 15 within a 4x4 coordinate to sort them.", "Puzzle Games", false, "filter_frames"),
    MiniGame("matchstick", "Matchstick Math Move", "Move exactly one matchstick to correct skewed arithmetic calculations.", "Logic Games", false, "square"),
    MiniGame("target_shoot", "Target Archery Bullet", "Release the bow string in step to hit moving target centers.", "Reflex Games", false, "my_location"),
    MiniGame("brick_pile", "Tetri-Stack Puzzle", "Drop tetramino brick piles to clear fully packed lines.", "Arcade Games", false, "grid_3x3"),
    MiniGame("simon_speed", "Simon Fast Blink", "Repeat audio-visual rhythms operating at a triple warp factor.", "Memory Games", false, "bolt"),
    MiniGame("coin_collector", "Coin Vault Looter", "Sling a pocket magnet to collect gold coins in visual patterns.", "Satisfying Games", false, "currency_exchange"),
    MiniGame("color_harmony", "Gradient Color Hue", "Swap color spectrum blocks to arrange a perfect continuous hue ramp.", "Puzzle Games", false, "gradient"),
    MiniGame("binary_math", "Binary Code Binary", "Convert zero and one sequences to decimal numbers on the fly.", "Number Games", false, "code"),
    MiniGame("planet_orbit", "Orbit Planet Gravity", "Tap to inject celestial satellite systems without collision.", "Physics Games", false, "blur_linear"),
    MiniGame("tap_speed", "Rapid Fire Tap FPS", "Double finger mash target zones within 10 frantic seconds.", "Reflex Games", false, "touch_app")
)
