package com.lcy0x1.core.magic;

import com.lcy0x1.core.magic.HexHandler.Cell;
import com.lcy0x1.core.magic.HexHandler.Direction;
import com.lcy0x1.core.magic.HexHandler.HexException;
import com.lcy0x1.core.magic.HexHandler.SubHexCore;
import com.lcy0x1.core.math.Frac;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HexTest {

    /**
     * test looping stream, passed
     */
    public static HexHandler CH6() {
        HexHandler hex = new HexHandler(2);
        Cell c = hex.new Cell(0, 0);
        c.toggle(Direction.LOWER_RIGHT);
        c.walk(Direction.LOWER_RIGHT);
        c.toggle(Direction.LOWER_LEFT);
        c.toggle(Direction.RIGHT);
        c.walk(Direction.RIGHT);
        c.toggle(Direction.UPPER_RIGHT);
        c.toggle(Direction.LOWER_RIGHT);
        c.walk(Direction.LOWER_RIGHT);
        c.toggle(Direction.RIGHT);
        c.toggle(Direction.LOWER_LEFT);
        c.walk(Direction.LOWER_LEFT);
        c.toggle(Direction.LOWER_RIGHT);
        c.toggle(Direction.LEFT);
        c.walk(Direction.LEFT);
        c.toggle(Direction.LOWER_LEFT);
        c.toggle(Direction.UPPER_LEFT);
        c.walk(Direction.UPPER_LEFT);
        c.toggle(Direction.LEFT);
        return hex;
    }

    /**
     * 6F
     */
    public static HexHandler comp0006F() throws HexException {
        HexHandler hex = new HexHandler(3);
        HexHandler a = new HexHandler(2);
        HexHandler b = new HexHandler(2);
        HexHandler c = new HexHandler(2);
        HexHandler d = new HexHandler(2);

        setRow(a, 0, 4, 6, 2);
        setRow(a, 1, 2, 4, 5, 4);
        setRow(a, 2, 0, 1, 4, 4, 4);
        setRow(a, 3, 0, 7, 5);
        setRow(a, 4, 0, 1);

        setRow(b, 0, 2);
        setRow(b, 1, 0, 5, 2);
        setRow(b, 2, 1, 1, 7, 1, 1);
        setRow(b, 3, 0, 4, 2);

        setRow(c, 0, 0, 0, 4);
        setRow(c, 1, 5, 5, 4);
        setRow(c, 2, 0, 6, 2, 0, 4);
        setRow(c, 3, 2, 1, 3);

        setRow(d, 0, 2, 0, 4);
        setRow(d, 1, 0, 4, 2);
        setRow(d, 2, 1, 0, 0, 1);

        setRow(hex, 0, 2, 0, 0, 4);
        setRow(hex, 1, 3, 7, 7, 7, 4);
        setRow(hex, 2, 3, 5, 6, 2, 7, 4);
        setRow(hex, 3, 1, 3, 4, 5, 3, 5);
        setRow(hex, 4, 0, 3, 7, 3, 4);
        setRow(hex, 5, 0, 7, 7, 6);

        HexHandler.SubHexCore ca = hex.new SubHexCore(a);
        HexHandler.SubHexCore cb = hex.new SubHexCore(b);
        HexHandler.SubHexCore cc = hex.new SubHexCore(c);
        HexHandler.SubHexCore cd = hex.new SubHexCore(d);

        for (int i = 0; i < 3; i++) {
            Direction dl = Direction.LEFT.next(i * 2);
            Direction dll = Direction.LOWER_LEFT.next(i * 2);
            Direction dlr = Direction.LOWER_RIGHT.next(i * 2);
            Direction dr = Direction.RIGHT.next(i * 2);
            HexHandler.Cell cell = hex.new Cell(3, 3);
            cell.walk(dll);
            cell.set(cc, i * 2, false);
            cell.walk(dl);
            cell.set(cd, i * 2 + 1, false);
            cell.walk(dlr);
            cell.set(ca, i * 2, false);
            cell.walk(dr);
            cell.set(cb, i * 2, false);
            cell.walk(dr);
            cell.set(ca, i * 2 + 3, true);
        }
        return hex;
    }

    /**
     * 1-1-4F
     */
    public static HexHandler comp0114F() throws HexException {
        HexHandler hex = new HexHandler(3);
        HexHandler a = new HexHandler(2);
        HexHandler b = new HexHandler(2);
        HexHandler c = new HexHandler(2);
        HexHandler d = new HexHandler(2);
        HexHandler e = new HexHandler(2);

        setRow(a, 0, 2, 3);
        setRow(a, 1, 0, 5, 4);
        setRow(a, 2, 0, 5, 5, 1);
        setRow(a, 3, 2, 2);
        setRow(a, 4, 0, 1);

        setRow(b, 0, 2);
        setRow(b, 1, 0, 5, 2);
        setRow(b, 2, 1, 1, 7, 1, 1);
        setRow(b, 3, 0, 4, 2);

        setRow(c, 0, 0, 3);
        setRow(c, 1, 5, 2, 6);
        setRow(c, 2, 0, 0, 6, 4, 4);
        setRow(c, 3, 0, 4, 3);

        setRow(d, 0, 4, 6, 4);
        setRow(d, 1, 2, 4, 1, 4);
        setRow(d, 2, 1, 1, 5, 5, 1);
        setRow(d, 3, 0, 7);
        setRow(d, 4, 0, 1);

        setRow(e, 0, 2, 0, 2);
        setRow(e, 1, 0, 1, 1, 6);
        setRow(e, 2, 0, 0, 0, 4);
        setRow(e, 3, 0, 0, 2);

        setRow(hex, 0, 2, 0, 0, 4);
        setRow(hex, 1, 3, 7, 4, 6);
        setRow(hex, 2, 3, 6, 6, 4, 2);
        setRow(hex, 3, 1, 3, 5, 7, 1, 2, 4);
        setRow(hex, 4, 0, 3, 7, 3, 4);
        setRow(hex, 5, 0, 7, 7, 6);

        HexHandler.SubHexCore ca = hex.new SubHexCore(a);
        HexHandler.SubHexCore cb = hex.new SubHexCore(b);
        HexHandler.SubHexCore cc = hex.new SubHexCore(c);
        HexHandler.SubHexCore cd = hex.new SubHexCore(d);
        HexHandler.SubHexCore ce = hex.new SubHexCore(e);

        setSub(hex, ca, 1, 1, 2, false);
        setSub(hex, ca, 3, 1, 5, true);
        setSub(hex, cb, 2, 1, 5, true);
        setSub(hex, cc, 3, 2, 5, true);

        setSub(hex, ca, 5, 1, 0, false);
        setSub(hex, ca, 5, 3, 3, true);
        setSub(hex, cb, 5, 2, 0, false);
        setSub(hex, cc, 4, 2, 0, false);

        setSub(hex, ce, 4, 1, 0, false);
        setSub(hex, cd, 3, 3, 0, false);

        return hex;
    }

    /**
     * 3F-3F
     */
    public static HexHandler comp3F3F() throws HexException {
        HexHandler hex = new HexHandler(3);
        HexHandler a = new HexHandler(2);
        HexHandler b = new HexHandler(2);
        HexHandler c = new HexHandler(2);

        setRow(a, 0, 0, 6, 2);
        setRow(a, 1, 0, 4, 3);
        setRow(a, 2, 0, 7, 1, 5);
        setRow(a, 3, 2, 2, 4);
        setRow(a, 4, 0, 1);

        setRow(b, 0, 2, 0, 4);
        setRow(b, 1, 0, 4, 2);
        setRow(b, 2, 2, 5, 7, 2, 4);
        setRow(b, 3, 0, 4, 2);

        setRow(c, 0, 2, 0, 4);
        setRow(c, 1, 5, 3, 2);
        setRow(c, 2, 0, 0, 5, 3);
        setRow(c, 3, 0, 4, 0, 4);

        setRow(hex, 0, 2, 0, 0, 4);
        setRow(hex, 1, 3, 6, 0, 7, 4);
        setRow(hex, 2, 3, 7, 2, 5, 7, 4);
        setRow(hex, 3, 1, 1, 1, 7, 1, 1);
        setRow(hex, 4, 0, 0, 6, 6);
        setRow(hex, 5, 0, 7, 7, 6);

        HexHandler.SubHexCore ca = hex.new SubHexCore(a);
        HexHandler.SubHexCore cb = hex.new SubHexCore(b);
        HexHandler.SubHexCore cc = hex.new SubHexCore(c);

        for (int i = 0; i < 3; i++) {
            HexHandler.Cell cell = hex.new Cell(3, 3);
            cell.walk(Direction.LOWER_LEFT.next(i * 2), 2);
            cell.set(ca, i * 2, false);
            cell.walk(Direction.RIGHT.next(i * 2));
            cell.set(cb, i * 2, false);
            cell.walk(Direction.RIGHT.next(i * 2));
            cell.set(ca, i * 2 + 3, true);
        }
        setSub(hex, cc, 3, 3, 0, false);
        return hex;
    }

    /**
     * test crossing stream, passed
     */
    public static HexHandler cross() {
        HexHandler hex = new HexHandler(2);
        for (Direction d : Direction.values()) {
            Cell c = hex.new Cell(2, 2);
            // c.toggle(d);
            c.walk(d);
            c.toggle(d);
        }
        return hex;
    }

    public static HexHandler dead() {
        HexHandler hex = new HexHandler(2);
        HexHandler.Cell cell = hex.new Cell(0, 0);
        cell.toggle(Direction.LOWER_RIGHT);
        cell.walk(Direction.LOWER_RIGHT);
        cell.toggle(Direction.LOWER_RIGHT);
        cell.walk(Direction.LOWER_RIGHT);
        cell.toggle(Direction.UPPER_RIGHT);
        cell.toggle(Direction.LOWER_RIGHT);
        cell.walk(Direction.LOWER_RIGHT);
        cell.toggle(Direction.UPPER_RIGHT);
        cell.walk(Direction.UPPER_RIGHT);
        cell.toggle(Direction.UPPER_LEFT);
        return hex;
    }

    public static void main(String[] args) throws Exception {
        // testHex();
        // testRender();
        int max = 0;
        for (int i = 2; i < 4096; i++) {
            int c = 0;
            for (int j = 0; j < Math.sqrt(i); j++) {
                int k = (int) Math.sqrt(i - j * j);
                if (k < j)
                    break;
                if (k * k + j * j == i) {
                    if (j == 0)
                        c += 4;
                    else
                        c += k == j ? 4 : 8;
                }
            }
            if (c > max) {
                System.out.print(i + ": " + c + "\t" + 1.0 * c / i + "\t");
                for (int j = 0; j < Math.sqrt(i); j++) {
                    int k = (int) Math.sqrt(i - j * j);
                    if (k < j)
                        break;
                    if (k * k + j * j == i)
                        System.out.print("(" + j + "," + k + ")\t");
                }
                System.out.println();
                max = c;
            }
        }
        max = 0;
        for (int i = 2; i < 4096; i++) {
            int c = 0;
            for (int j = 0; j < Math.sqrt(i); j++)
                for (int s = 0; s <= j; s++) {
                    int k = (int) Math.sqrt(i - j * j - s * s);
                    if (k < j)
                        break;
                    if (k * k + j * j + s * s == i) {
                        if (j == 0 && s == 0)
                            c += 6;
                        else if (s == 0)
                            c += j == k ? 12 : 24;
                        else
                            c += k == j && j == s ? 8 : k == j || j == s ? 24 : 48;
                    }
                }
            if (c > max) {
                System.out.print(i + ": " + c + "\t" + 1.0 * c / i + "\t");
                for (int j = 0; j < Math.sqrt(i); j++)
                    for (int s = 0; s <= j; s++) {
                        int k = (int) Math.sqrt(i - j * j - s * s);
                        if (k < j)
                            break;
                        if (k * k + j * j + s * s == i)
                            System.out.print("(" + s + "," + j + "," + k + ")\t");
                    }
                System.out.println();
                max = c;
            }
        }
    }

    /**
     * 1-2
     */
    public static HexHandler st0012() {
        HexHandler hex = new HexHandler(2);
        Cell c0 = hex.new Cell(0, 0);
        c0.toggle(Direction.LOWER_RIGHT);
        c0.walk(Direction.LOWER_RIGHT);
        c0.toggle(Direction.LOWER_RIGHT);
        c0.walk(Direction.LOWER_RIGHT);
        c0.toggle(Direction.LOWER_RIGHT);
        c0.walk(Direction.LOWER_RIGHT);
        c0.toggle(Direction.UPPER_RIGHT);
        Cell c1 = hex.new Cell(4, 0);
        c1.toggle(Direction.UPPER_RIGHT);
        c1.walk(Direction.UPPER_RIGHT);
        c1.toggle(Direction.UPPER_RIGHT);
        c1.walk(Direction.UPPER_RIGHT);
        c1.toggle(Direction.UPPER_RIGHT);
        c1.walk(Direction.UPPER_RIGHT);
        c1.toggle(Direction.UPPER_LEFT);
        c1.walk(Direction.UPPER_LEFT);
        c1.toggle(Direction.LOWER_LEFT);
        Cell c2 = hex.new Cell(2, 2);
        c2.toggle(Direction.RIGHT);
        c2.walk(Direction.RIGHT);
        c2.toggle(Direction.RIGHT);
        return hex;
    }

    /**
     * 1-5
     */
    public static HexHandler st0015() {
        HexHandler hex = new HexHandler(3);
        set(hex, 0, 0, 0);
        set(hex, 0, 1, 0, 1);
        set(hex, 0, 2, 1);
        set(hex, 0, 3, 1);

        set(hex, 1, 1, 1, 2);
        set(hex, 1, 2, 2);
        set(hex, 1, 3, 0);
        set(hex, 1, 4, 1);

        set(hex, 2, 0, 0, 1);
        set(hex, 2, 1, 0, 1);
        set(hex, 2, 4, 0, 1);

        set(hex, 3, 0, 1);
        set(hex, 3, 1, 2);
        set(hex, 3, 2, 0, 2);
        set(hex, 3, 3, 1);
        set(hex, 3, 4, 0, 1);
        set(hex, 3, 5, 0, 1, 2);

        set(hex, 4, 0, 1);
        set(hex, 4, 1, 0, 2);
        set(hex, 4, 2, 2);
        set(hex, 4, 3, 0, 2);
        set(hex, 4, 4, 2);
        set(hex, 4, 5, 2);

        set(hex, 5, 1, 0, 1, 2);
        set(hex, 5, 2, 1);
        set(hex, 5, 3, 0, 2);

        set(hex, 6, 1, 0);
        set(hex, 6, 2, 0);

        return hex;
    }

    /**
     * 2-3
     */
    public static HexHandler st0023() {
        HexHandler hex = new HexHandler(3);
        set(hex, 0, 0, 1);
        set(hex, 0, 2, 2);
        set(hex, 0, 3, 1);

        set(hex, 1, 0, 1, 2);
        set(hex, 1, 1, 1, 2);
        set(hex, 1, 2, 0, 2);
        set(hex, 1, 3, 1);
        set(hex, 1, 4, 1);

        set(hex, 2, 0, 0);
        set(hex, 2, 1, 0, 1, 2);
        set(hex, 2, 3, 0, 2);
        set(hex, 2, 4, 2);
        set(hex, 2, 5, 2);

        set(hex, 3, 0, 0);
        set(hex, 3, 1, 0, 1);
        set(hex, 3, 2, 0);
        set(hex, 3, 3, 1);
        set(hex, 3, 4, 0);
        set(hex, 3, 5, 2);
        set(hex, 3, 6, 2);

        set(hex, 4, 1, 1);
        set(hex, 4, 3, 0, 2);
        set(hex, 4, 4, 0, 2);
        set(hex, 4, 5, 2);

        set(hex, 5, 1, 1);
        set(hex, 5, 2, 1);
        set(hex, 5, 3, 0, 2);

        set(hex, 6, 1, 0);
        set(hex, 6, 2, 0);

        return hex;
    }

    /**
     * 3-3
     */
    public static HexHandler st0033() {
        HexHandler hex = new HexHandler(4);
        setRow(hex, 0, 2, 3, 1, 1);
        setRow(hex, 1, 6, 7, 3, 1, 1, 2);
        setRow(hex, 2, 1, 7, 1, 7, 5, 0, 2);
        setRow(hex, 3, 5, 1, 2, 4, 4, 0, 0, 4);
        setRow(hex, 4, 0, 5, 1, 0, 4, 7, 1, 0, 4);
        setRow(hex, 5, 2, 0, 0, 6, 4, 3, 5);
        setRow(hex, 6, 2, 1, 1, 3, 3, 7, 4);
        setRow(hex, 7, 1, 1, 1, 3, 2);
        setRow(hex, 8, 1, 1, 1);
        return hex;
    }

    /**
     * 1-1-4
     */
    public static HexHandler st0114() {
        HexHandler hex = new HexHandler(3);
        setRow(hex, 0, 2, 0, 0, 2);
        setRow(hex, 1, 0, 2, 0, 0, 2);
        setRow(hex, 2, 0, 0, 6, 0, 3);
        setRow(hex, 3, 1, 3, 0, 2, 3, 7);
        setRow(hex, 4, 0, 1, 4, 5, 4, 4);
        setRow(hex, 5, 0, 7, 2, 5);
        setRow(hex, 6, 0, 1, 1);
        return hex;
    }

    /**
     * 1-2-3
     */
    public static HexHandler st0123() {
        HexHandler hex = new HexHandler(3);
        setRow(hex, 0, 2, 0, 4, 2);
        setRow(hex, 1, 6, 6, 5, 6, 6);
        setRow(hex, 2, 1, 7, 0, 2, 0, 4);
        setRow(hex, 3, 1, 3, 1, 1, 4, 4, 4);
        setRow(hex, 4, 0, 1, 5, 5, 5);
        setRow(hex, 5, 0, 7, 2, 4);
        setRow(hex, 6, 0, 1, 1);
        return hex;
    }

    /**
     * 1-3-2
     */
    public static HexHandler st0132() {
        HexHandler hex = new HexHandler(3);
        setRow(hex, 0, 4, 0, 0, 2);
        setRow(hex, 1, 5, 1, 6, 0, 2);
        setRow(hex, 2, 1, 3, 2, 2, 0, 4);
        setRow(hex, 3, 1, 7, 0, 2, 1, 4, 4);
        setRow(hex, 4, 2, 5, 4, 5, 5, 4);
        setRow(hex, 5, 2, 2, 2, 5);
        setRow(hex, 6, 0, 1, 1);
        return hex;
    }

    /**
     * 1-1-1-3
     */
    public static HexHandler st1113() {
        HexHandler hex = new HexHandler(3);
        setRow(hex, 0, 1, 2, 0, 2);
        setRow(hex, 1, 0, 3, 6, 0, 2);
        setRow(hex, 2, 0, 0, 6, 5, 4, 4);
        setRow(hex, 3, 2, 0, 4, 1, 5, 4, 4);
        setRow(hex, 4, 3, 5, 5, 5, 5);
        setRow(hex, 5, 0, 7, 2, 4);
        setRow(hex, 6, 0, 1, 1);
        return hex;
    }

    /**
     * 1-1-2-2
     */
    public static HexHandler st1122() {
        HexHandler hex = new HexHandler(3);
        set(hex, 0, 0, 1);
        set(hex, 0, 1, 0, 1);
        set(hex, 0, 2, 0, 2);

        set(hex, 1, 0, 1, 2);
        set(hex, 1, 1, 0, 1);
        set(hex, 1, 2, 0, 1, 2);
        set(hex, 1, 3, 1);
        set(hex, 1, 4, 1, 2);

        set(hex, 2, 0, 0, 2);
        set(hex, 2, 1, 0, 1);
        set(hex, 2, 3, 0, 2);
        set(hex, 2, 4, 0, 1, 2);
        set(hex, 2, 5, 0, 1);

        set(hex, 3, 2, 1);
        set(hex, 3, 3, 1);
        set(hex, 3, 4, 1);
        set(hex, 3, 5, 1);

        set(hex, 4, 0, 0, 1);
        set(hex, 4, 1, 0, 1);
        set(hex, 4, 2, 0, 1);
        set(hex, 4, 3, 0, 1);
        set(hex, 4, 4, 1, 2);
        set(hex, 4, 5, 2);

        set(hex, 5, 0, 1);
        set(hex, 5, 1, 1);
        set(hex, 5, 2, 1);
        set(hex, 5, 3, 2);
        set(hex, 5, 4, 2);

        set(hex, 6, 1, 0);
        return hex;
    }

    /**
     * 1-2-1-2
     */
    public static HexHandler st1212() {
        HexHandler hex = new HexHandler(3);
        setRow(hex, 0, 2, 3, 5);
        setRow(hex, 1, 6, 3, 7, 1, 2);
        setRow(hex, 2, 5, 3, 0, 4, 5, 6);
        setRow(hex, 3, 0, 5, 3, 5, 2, 2);
        setRow(hex, 4, 2, 6, 4, 0, 2, 4);
        setRow(hex, 5, 2, 0, 0, 0, 4);
        return hex;
    }

    /**
     * pending: 0013,0014,0024,0222
     */
    public static void testHex() throws HexException {
        HexHandler hex = comp3F3F();
        long t0 = System.currentTimeMillis();
        HexHandler.FlowChart calc = hex.getMatrix(true);
        long t1 = System.currentTimeMillis();
        System.out.println(t1 - t0);
        Frac[][] ans = calc.matrix;
        for (int i = 0; i < 6; i++) {
            System.out.print("output " + i + ": ");
            for (int j = 0; j < 6; j++) {
                System.out.print("\t" + ans[i][j]);
            }
            System.out.println();
        }
        for (HexHandler.FlowChart.Flow f : calc.flows) {
            System.out.print(f.arrow + "\t -> ");
            for (int j = 0; j < 6; j++) {
                System.out.print("\t" + f.forward[j]);
            }
            System.out.println();
            System.out.print(f.arrow + "\t <- ");
            for (int j = 0; j < 6; j++) {
                System.out.print("\t" + f.backward[j]);
            }
            System.out.println();
        }
    }

    public static void testRender() throws Exception {
        HexHandler hex = comp0006F();
        int r = 50;

        Graphics[] gs = new Graphics[1];
        Map<String, BufferedImage> map = new HashMap<>();
        HexRenderer.Renderer renderer = (x, y, str) -> {
            if (!map.containsKey(str)) {
                try {
                    BufferedImage spr = ImageIO.read(new File("./sprites/" + str + ".png"));
                    map.put(str, spr);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(str);
                }
            }
            gs[0].drawImage(map.get(str), r + x, r + y, null);
        };
        HexRenderer hr = new HexRenderer(renderer);
        hr.hex = hex;
        // hr.flow = hex.getMatrix(true);
        for (int i = 0; i < 6; i++) {
            BufferedImage bimg = new BufferedImage(r * 2, r * 2, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = bimg.getGraphics();
            gs[0] = g;
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, r * 2, r * 2);
            hr.renderHex();
            g.dispose();
            File f = new File("./out/hex" + i + ".png");
            if (!f.exists())
                f.createNewFile();
            ImageIO.write(bimg, "PNG", f);
        }
    }

    /**
     * test unifying stream, passed
     */
    public static HexHandler unify() {
        HexHandler hex = new HexHandler(2);
        Cell c = hex.new Cell(2, 0);
        for (int i = 0; i < 4; i++) {
            c.toggle(Direction.RIGHT);
            c.walk(Direction.RIGHT);
        }
        c = hex.new Cell(0, 0);
        for (int i = 0; i < 2; i++) {
            c.toggle(Direction.LOWER_RIGHT);
            c.walk(Direction.LOWER_RIGHT);
        }
        c = hex.new Cell(0, 2);
        c.toggle(Direction.LOWER_LEFT);
        c.walk(Direction.LOWER_LEFT);
        c.toggle(Direction.LOWER_RIGHT);
        return hex;
    }

    private static void set(HexHandler hex, int r, int c, int... dirs) {
        for (int dir : dirs)
            hex.new Cell(r, c).toggle(Direction.values()[dir]);
    }

    private static void setRow(HexHandler hex, int r, int... cells) {
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < 3; j++)
                if ((cells[i] & 1 << j) > 0)
                    hex.new Cell(r, i).toggle(Direction.values()[j]);
    }

    private static void setSub(HexHandler hex, SubHexCore sub, int r, int c, int rot, boolean f) {
        try {
            hex.new Cell(r, c).set(sub, rot, f);
        } catch (HexException e) {
            e.printStackTrace();
        }
    }

}
