package Src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class cell {

    public final int row;
    public final int column;
    public cell north, south, east, west;
    private final Map<cell, Boolean> links;
    public cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.links = new HashMap<>();
    }

    public cell link(cell cell, boolean bidi) {
        links.put(cell, true);
        if (bidi) {
            cell.link(this, false);
        }
        return this;
    }

    public cell link(cell cell) {
        return link(cell, true);
    }

    public cell unlink(cell cell, boolean bidi) {
        links.remove(cell);
        if (bidi) {
            cell.unlink(this, false);
        }
        return this;
    }

    public cell unlink(cell cell) {
        return unlink(cell, true);
    }


    public Set<cell> links() {
        return links.keySet();
    }

    public boolean isLinked(cell cell) {
        return links.containsKey(cell);
    }


    public List<cell> neighbors() {
        List<cell> list = new ArrayList<>();
        if (north != null) list.add(north);
        if (south != null) list.add(south);
        if (east != null) list.add(east);
        if (west != null) list.add(west);
        return list;
    }


}