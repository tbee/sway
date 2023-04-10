package org.tbee.sway;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class JTreeTest {
    static public void main(String[] args) throws Exception {

        SwingUtilities.invokeAndWait(() -> {

            Person personCarl = new Person();
            personCarl.setName("carl");

            Person personMarry = new Person();
            personMarry.setName("marry");

            List<Person> persons = new ArrayList<>();
            persons.add(personCarl);
            persons.add(personMarry);

            List<TreeModelListener> treeModelListeners = new ArrayList<>();
            JTree jTree = new JTree(new TreeModel(){
                @Override
                public Object getRoot() {
                    return persons;
                }

                @Override
                public Object getChild(Object parent, int index) {
                    if (parent == persons) {
                        return persons.get(index);
                    }
                    return null;
                }

                @Override
                public int getChildCount(Object parent) {
                    if (parent == persons) {
                        return persons.size();
                    }
                    return 0;
                }

                @Override
                public boolean isLeaf(Object node) {
                    if (node == persons) {
                        return false;
                    }
                    return true;
                }

                @Override
                public void valueForPathChanged(TreePath path, Object newValue) {
                }

                @Override
                public int getIndexOfChild(Object parent, Object child) {
                    if (parent == persons) {
                        return persons.indexOf(child);
                    }
                    return 0;
                }


                public void addTreeModelListener(TreeModelListener l) {
                    treeModelListeners.add(l);
                }
                @Override
                public void removeTreeModelListener(TreeModelListener l) {
                    treeModelListeners.remove(l);
                }
            });

            JButton button = new JButton("change it");
            button.addActionListener(e -> {
                personCarl.setName(personCarl.getName() + "x");
                System.out.println(personCarl);

                TreePath treePath = new TreePath(new Object[]{persons, personCarl});
                treeModelListeners.forEach(tml -> tml.treeNodesChanged(new TreeModelEvent(jTree.getModel(), treePath)));
            });

            JFrame jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.getContentPane().setLayout(new FlowLayout());
            jFrame.getContentPane().add(new JScrollPane(jTree));
            jFrame.getContentPane().add(button);
            jFrame.pack();
            jFrame.setVisible(true);
        });
    }
}