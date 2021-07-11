package com.hikarishima.lightland.config.road;

public class CommercialRoadBuilder {

    private static final int OFFSET = 2;

    private static int cache_x;
    private static int cache_z;

    public static float[][] cache_road;

    private static float[][] getCommercialRoad(int cx, int cz) {
        if (cx == cache_x && cz == cache_z && cache_road != null)
            return cache_road;
        cache_x = cx;
        cache_z = cz;
        int n = 4 + OFFSET * 2;
        int[][] pixels = new int[n][n];

        for (int x = 0; x < n; x++)
            for (int z = 0; z < n; z++) {
                int px = cx * 4 + x - OFFSET;
                int pz = cz * 4 + z - OFFSET;
                pixels[x][z] = ImageRoadReader.RoadType.getCommercial(ImageRoadReader.getRoadPixel(px, pz));
            }
        cache_road = new float[16][16];
        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++) {
                for (int ix = -2; ix <= 2; ix++)
                    for (int iz = -2; iz <= 2; iz++) {
                        float dx = (x + ix & 3) / 4f;
                        float dz = (z + iz & 3) / 4f;

                    }
            }
        return cache_road;
    }


}
