package com.hikarishima.lightland.world;

public class ImageRoadReader {

    private static final int FILL_UL = 0b1110111011000000;
    private static final int FILL_UP = 0b1111111101100000;
    private static final int FILL_SUL = 0b1100100000000000;
    private static final int FILL_UCW = 0b1111111100110000;
    private static final int FILL_UCCW = 0b1111111111000000;

    private static int rotate(int fill, int a) {
        if (a == 0)
            return fill;
        else if (a == 1) {
            int ans = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ans |= ((fill >> (15 - i * 4 - j)) & 1) << (12 - j * 4 + i);
            return ans;
        } else if (a == 2) {
            int ans = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ans |= ((fill >> (15 - i * 4 - j)) & 1) << (i * 4 + j);
            return ans;
        } else {
            int ans = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ans |= ((fill >> (15 - i * 4 - j)) & 1) << (3 + j * 4 - i);
            return ans;
        }
    }

    private enum ConnDire {
        UL(-1, -1, 0),
        UP(-1, 0, 1),
        UR(-1, 1, 2),
        LEFT(0, -1, 7),
        RIGHT(0, 1, 3),
        DL(1, -1, 6),
        DOWN(1, 0, 5),
        DR(1, 1, 4);

        public static final ConnDire[] ADJS = {UL, UP, UR, RIGHT, DR, DOWN, DL, LEFT};

        public final int dx, dz, a, fill;

        ConnDire(int dx, int dz, int a) {
            this.dx = dx;
            this.dz = dz;
            this.a = a;
            this.fill = ImageRoadReader.rotate(a % 2 == 0 ? FILL_UL : FILL_UP, a / 2);
        }

        public boolean isDiag() {
            return dx * dz != 0;
        }

        public ConnDire getHor() {
            return getDire(dx, 0);
        }

        public ConnDire getVer() {
            return getDire(0, dz);
        }

        public ConnDire rotate(int ind) {
            return ADJS[(a + ind + 8) % 8];
        }

        public int mask() {
            return 1 << ordinal();
        }

        public boolean isIn(int mask) {
            return (mask & mask()) != 0;
        }

        public static ConnDire getDire(int dx, int dz) {
            int a = (dx + 1) * 3 + (dz + 1);
            if (a > 3) a--;
            return ConnDire.values()[a];
        }
    }

    private enum SideDire {
        SUL(-1, -1),
        SUR(-1, 1),
        SDR(1, 1),
        SDL(1, -1);

        public final int dx, dz, fill;

        SideDire(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
            this.fill = ImageRoadReader.rotate(FILL_SUL, ordinal());
        }

        public ConnDire getHor() {
            return ConnDire.getDire(dx, 0);
        }

        public ConnDire getVer() {
            return ConnDire.getDire(0, dz);
        }

    }

    private enum SpecDire {
        UCW(-1, 0),
        UCCW(-1, 0),
        RCW(0, 1),
        RCCW(0, 1),
        DCW(1, 0),
        DCCW(1, 0),
        LCW(0, -1),
        LCCW(0, -1);

        public final int dx, dz, fill;

        SpecDire(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
            this.fill = ImageRoadReader.rotate(ordinal() % 2 == 0 ? FILL_UCW : FILL_UCCW, ordinal() / 2);
        }

    }

    private static int cache_x, cache_z;
    private static int[] cache_val;

    private static int special_road(int types) {
        for (int i = 0; i < 4; i++) {
            ConnDire cd = ConnDire.ADJS[i * 2 + 1];
            if (!cd.isDiag() && cd.isIn(types)) {
                int cw = cd.mask() | cd.rotate(-1).mask() | cd.rotate(2).mask();
                int ccw = cd.mask() | cd.rotate(1).mask() | cd.rotate(-2).mask();
                if (types == cw)
                    return SpecDire.values()[i * 2].fill;
                if (types == ccw)
                    return SpecDire.values()[i * 2 + 1].fill;
            }
        }
        return -1;
    }

    private static int node_road(int types) {
        int conn = 0;
        int mask = 0;
        for (ConnDire cd : ConnDire.ADJS)
            if (cd.isDiag() && cd.isIn(types) && cd.getHor().isIn(types) && cd.getVer().isIn(types)) {
                conn |= cd.fill;
                mask |= cd.mask() | cd.getHor().mask() | cd.getVer().mask();
            }
        types = (types | mask) - mask;
        mask = 0;
        for (ConnDire cd : ConnDire.ADJS)
            if (!cd.isDiag() && cd.isIn(types)) {
                conn |= cd.fill;
                mask |= cd.mask() | cd.getHor().mask() | cd.getVer().mask();
            }
        types = (types | mask) - mask;
        for (ConnDire cd : ConnDire.ADJS)
            if (cd.isDiag() && cd.isIn(types)) {
                conn |= cd.fill;
            }
        return conn;
    }

    public static int[] getRuralRoad(int x, int z) {
        if (cache_x == x && cache_z == z && cache_val != null)
            return cache_val;

        ImageBiomeReader.RoadType[][] area = new ImageBiomeReader.RoadType[6][6];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                area[i][j] = ImageBiomeReader.getRoadPixel(x * 4 - 1 + i, z * 4 - 1 + j);

        int[] ans = new int[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                int types = 0;
                for (ConnDire cd : ConnDire.ADJS) {
                    if (area[i + 1 + cd.dx][j + 1 + cd.dz] == ImageBiomeReader.RoadType.RURAL) {
                        types |= cd.mask();
                    }
                }
                if (area[i + 1][j + 1] == ImageBiomeReader.RoadType.RURAL) {
                    int special = special_road(types);
                    ans[i * 4 + j] = special >= 0 ? special : node_road(types);
                } else if (area[i + 1][j + 1] == ImageBiomeReader.RoadType.NONE) {
                    int side = 0;
                    for (SideDire cd : SideDire.values())
                        if (cd.getHor().isIn(types) && cd.getVer().isIn(types)) {
                            side |= cd.fill;
                        }
                    ans[i * 4 + j] = side;
                }
            }

        cache_x = x;
        cache_z = z;
        cache_val = ans;
        return ans;
    }

    public static boolean onRoad(int x, int z) {
        int[] roads = getRuralRoad(x >> 4, z >> 4);
        int cx = x & 15;
        int cz = z & 15;
        int road = roads[cx / 4 * 4 + cz / 4];
        return ((road >> (15 - (cx & 3) * 4 - (cz & 3)) & 1)) == 1;
    }

}
