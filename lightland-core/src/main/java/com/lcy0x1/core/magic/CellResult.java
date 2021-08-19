package com.lcy0x1.core.magic;

import lombok.Getter;

@Getter
public class CellResult extends LocateResult {

    private final int row, cell;
    private final HexHandler hex;

    CellResult(int row, int cell, HexHandler hex) {
        this.row = row;
        this.cell = cell;
        this.hex = hex;
    }

    public static CellResult get(int row, int cell, HexHandler hex) {
        if (row < 0 || row >= hex.getRowCount())
            return null;
        if (cell < 0 || cell >= hex.getCellCount(row))
            return null;
        return new CellResult(row, cell, hex);
    }

    @Override
    public ResultType getType() {
        return ResultType.CELL;
    }

    @Override
    public double getX() {
        return hex.getX(row, cell);
    }

    @Override
    public double getY() {
        return hex.getY(row, cell);
    }

}
