/**
 * Java test harness for throughput experiments on concurrent data structures.
 * Copyright (C) 2012 Trevor Brown
 * Contact (tabrown [at] cs [dot] toronto [dot edu]) with any questions or comments.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package adapters;

import main.support.BBSTInterface;
import main.support.KSTNode;
import main.support.OperationListener;
import main.support.Random;
import java.util.Iterator;
import java.util.concurrent.ConcurrentNavigableMap;
import algorithms.bronson.snaptree.SnapTreeMap;
import java.util.ArrayList;

/**
 *
 * @author trev
 */
public class SnapTreeAdapter<K extends Comparable<? super K>> extends AbstractAdapter<K> implements BBSTInterface<K> {
    final public SnapTreeMap<K,K> tree = new SnapTreeMap<K,K>();
    
    public SnapTreeAdapter() {

    }

    public final boolean add(final K key, final Random rng) {
        tree.put(key, key);
        return true;
    }

    public final K get(final K key) {
        return tree.get(key);
    }

    public final boolean remove(final K key, final Random rng) {
        return tree.remove(key) != null;
    }

    public final boolean contains(final K key) {
        return tree.containsKey(key);
    }

    public final int size() {
        return tree.size();
    }

    public final void addListener(final OperationListener l) {
        
    }

    public final KSTNode<K> getRoot() {
        return null;//tree.getRoot();
    }

    public final int getSumOfDepths() {
        return 0;//tree.getSumOfDepths();
    }

    public final int sequentialSize() {
        return tree.size();
    }

    
    @Override
    public final int rangeQuery(final K lo, final K hi, final int rangeSize, final Random rng) {
        final ConcurrentNavigableMap map = tree.subMap(lo, true, hi, true);
        final Iterator<K> it = map.keySet().iterator();
        ArrayList<K> copy = new ArrayList<>();
        while (it.hasNext())
            copy.add(it.next());
        return copy.size();
    }

    @Override
    public final Object partialSnapshot(final int size, final Random rng) {
        assert size == main.Globals.DEFAULT_RQ_SIZE;
        final Object[] result = new Object[size];
        final Iterator it = tree.keySet().iterator();
        int i = 0;
        while (i < size && it.hasNext()) {
            result[i++] = it.next();
        }
        return result;
    }

}
