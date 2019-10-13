package mirea.structures;

public class CustomSet<E> {
    private int size;
    private CustomList<CustomList<E>> bucketList;

    public int size() {
        return size;
    }

    public CustomSet() {
        this.size = 16;
        this.bucketList = new CustomList<>();
        createBuckets();
    }

    private void createBuckets() {
        for (int i=0; i<size(); i++) {
            bucketList.add(new CustomList<>());
        }
    }

    public boolean add(E o) {
        int index = Math.abs(o.hashCode() % bucketList.size());
        for (int i=0; i<bucketList.get(index).size(); i++) {
            if (bucketList.get(index).get(i).equals(o)) {
                return false;
            }
        }
        bucketList.get(index).add(o);
        return true;
    }

    public boolean contains(E o) {
        int index = Math.abs(o.hashCode() % bucketList.size());
        for (int i=0; i<bucketList.get(index).size(); i++) {
            if (bucketList.get(index).get(i).equals(o)) {
                return true;
            }
        }
        return false;
    }
}
