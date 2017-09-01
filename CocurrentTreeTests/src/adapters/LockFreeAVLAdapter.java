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
import algorithms.published.ConcurrentRelaxedAVLMap;

///**
// *
// * @author trev
// */
//public class LockFreeAVLAdapter<K extends Comparable<? super K>> extends AbstractAdapter<K> implements BBSTInterface<K> {
//    public AVLSearchTree<K,K> tree;
//
//    public LockFreeAVLAdapter() {
//        tree = new AVLSearchTree();
//    }
//
//    public LockFreeAVLAdapter(final int allowedViolations) {
//        tree = new AVLSearchTree(allowedViolations);
//    }
//
//    public boolean contains(K key) {
//        return tree.containsKey(key);
//    }
//    
//    public boolean add(K key, Random rng) {
////        return tree.putIfAbsent(key, key) == null;
//        tree.put(key, key); return true;
//    }
//
//    public K get(K key) {
//        return tree.get(key);
//    }
//
//    public boolean remove(K key, Random rng) {
//        return tree.remove(key) != null;
//    }
//
//    public void addListener(OperationListener l) {
////        tree.addListener(l);
//    }
//
//    public int size() {
//        return tree.sequentialSize();
//    }
//
//    public KSTNode<K> getRoot() {
//        return tree.getRoot();
//    }
//    
//    public double getAverageDepth() {
//        return tree.getSumOfDepths() / (double) tree.getNumberOfNodes();
//    }
//
//    public int getSumOfDepths() {
//        return tree.getSumOfDepths();
//    }
//
//    public int sequentialSize() {
//        return tree.sequentialSize();
//    }
//
//    public double getRebalanceProbability() {
//        return -1;
//    }
//
//    @Override
//    public String toString() {
//        return tree.toString();
//    }
//    
//}



/**
 *
 * @author trev
 */
public class LockFreeAVLAdapter<K extends Comparable<? super K>> extends AbstractAdapter<K> implements BBSTInterface<K> {
    public ConcurrentRelaxedAVLMap<K,K> tree;

    public LockFreeAVLAdapter() {
        tree = new ConcurrentRelaxedAVLMap();
    }

    public LockFreeAVLAdapter(final int allowedViolations) {
        tree = new ConcurrentRelaxedAVLMap(allowedViolations);
    }

    public boolean contains(K key) {
        return tree.containsKey(key);
    }
    
    public boolean add(K key, Random rng) {
//        return tree.putIfAbsent(key, key) == null;
        tree.put(key, key); return true;
    }

    public K get(K key) {
        return tree.get(key);
    }

    public boolean remove(K key, Random rng) {
        return tree.remove(key) != null;
    }

    public void addListener(OperationListener l) {
        
    }

    public int size() {
        return sequentialSize();
    }

    public KSTNode<K> getRoot() {
        return null;
    }
    
    public double getAverageDepth() {
        return 0;
    }

    public int getSumOfDepths() {
        return 0;
    }

    public int sequentialSize() {
        return tree.size();
    }

    public double getRebalanceProbability() {
        return -1;
    }

    @Override
    public String toString() {
        return tree.toString();
    }
    
}