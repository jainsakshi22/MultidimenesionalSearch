/**
 * @author Sakshi Jain
 */

package MDS;
import java.util.*;

public class MDS {

    /**
     * <p>entryMap: is a map with id as it's key and Entry as it's value</p>
     *
     */
    private Map<Long, Entry> entryMap;
    /**
     * <p>setMap: is a map of map with description as it's key and map of price and price's counter as it's value</p>
     */
    private Map<Long, Map<Money, Integer>> setMap;

    /**
     * <p>Inner class represents a product marketed by Amazon. A product has attributes like id, price and description.</p>
     */
    private static class Entry implements Comparable {
        private Long id;
        private Money price;
        private Set<Long> description;

        /**
         * <p>constructor for creating a product.</p>
         *
         * @param id          unique identifier of a product
         * @param price       price of the product
         * @param description description of a product
         */
        Entry(long id, Money price, Set<Long> description) {
            this.id = id;
            this.price = price;
            this.description = description;
        }

        /**
         * @return hashcode for the product based on it's id
         */
        @Override
        public int hashCode() {
            return Long.hashCode(this.id);
        }

        /**
         * @param o product to be compared
         * @return int value based upon comparision.
         * 1, if Object o's price is smaller than current object's price
         * 0, if Object o's price is equal to the current object's price
         * -1, if Object o's price is greater than current object's price
         */
        @Override
        public int compareTo(Object o) {
            return this.price.compareTo(((Entry) o).price);
        }

        /**
         * <p>toString() method to print product's price</p>
         *
         * @return price of the product in string format
         */
        @Override
        public String toString() {
            return price.toString();
        }
    }

    /**
     * <p>Constructor for MDS.MDS. Initializes entryMap and setMap for
     * the <b>product catalog</b></p>
     */

    public MDS() {
        entryMap = new HashMap<>();
        setMap = new HashMap<>();
    }

    /**
     * <p>Inserts a new product whose description is given
     * in the list.  If a product with the same id already exists, then its
     * description and price are replaced by the new values, unless list
     * is null or empty, in which case, just the price is updated.</p>
     *
     * @param id product's unique identifier
     * @param price product's price
     * @param list product's description
     * @return 1 if the item is new, and 0 otherwise.
     */
    public int insert(long id, Money price, java.util.List<Long> list) {
        int result = entryMap.get(id) == null ? 1 : 0;

        Entry e = result == 0 ? entryMap.get(id) : new Entry(id, price, new HashSet<>(list));
        if (result == 0) {
            for (long x : e.description) {
                if (setMap.get(x).get(e.price) != null) {
                    setMap.get(x).put(e.price, setMap.get(x).get(e.price) - 1);
                    if (setMap.get(x).get(e.price) <= 0) setMap.get(x).remove(e.price);
                }
            }
            if (list != null && list.size() > 0) {
                e.description.clear();
                e.description.addAll(list);

            }
            e.price = price;
        }
        for (Long x : e.description) {
            if (setMap.get(x) == null) {
                setMap.put(x, new TreeMap<>());
            }
            if (setMap.get(x).get(e.price) == null) {
                setMap.get(x).put(e.price, 1);
            } else {
                setMap.get(x).put(e.price, setMap.get(x).get(e.price) + 1);
            }
        }
        entryMap.put(id, e);

        return result;
    }

    /**
     * <p>Finds the product in the product catalog given it's id</p>
     *
     * @param id product's unique identifier
     * @return price of the product if found and 0 otherwise
     */
    public Money find(long id) {
        Money money = entryMap.get(id) != null ? entryMap.get(id).price : new Money();
        return money;
    }


    /**
     * <p>Deletes a product from the product catalog given it's id.</p>
     *
     * @param id product's unique identifier
     * @return the sum of the long ints that are in the description of the product deleted, or 0, if such an id did not exist in the product catalog.
     */
    public long delete(long id) {
        long sum = 0;
        if (entryMap.get(id) != null) {
            Entry e = entryMap.remove(id);
            for (long x : e.description) {
                sum += x;
                if (setMap.get(x).get(e.price) != null) {
                    setMap.get(x).put(e.price, setMap.get(x).get(e.price) - 1);
                    if (setMap.get(x).get(e.price) <= 0) setMap.get(x).remove(e.price);
                }
            }
        }
        return sum;
    }


    /**
     * <p>Find products whose description contains a given number (exact match with one of the long ints in the products's description)</p>
     *
     * @param n a number to be searched in description of all the products available in the product catalog.
     * @return lowest price of those products. Return 0 if there is no such product in the product catalog.
     */
    public Money findMinPrice(long n) {
        Money money = new Money();
        TreeMap<Money, Integer> map = (TreeMap<Money, Integer>) setMap.get(n);
        if (map != null && map.size() > 0) {
            money = map.firstKey();
        }
        return money;
    }


    /**
     * <p>Find products whose description contains a given number (exact match with one of the long ints in the products's description)</p>
     *
     * @param n a number to be searched in description of all the products available in the product catalog.
     * @return highest price of those products. Return 0 if there is no such product in the product catalog.
     */
    public Money findMaxPrice(long n) {
        Money money = new Money();
        TreeMap<Money, Integer> map = (TreeMap<Money, Integer>) setMap.get(n);
        if (map != null && map.size() > 0) {
            money = map.lastKey();

        }
        return money;
    }

    /**
     * <p>Find the number of products whose description contains n, and in addition, their prices fall within the given range, [low, high]</p>
     *
     * @param n a number to be searched in description of all the products available in the product catalog.
     * @param low lower limit of the product's price
     * @param high upper limit of the product's price
     * @return count of products for which matching criteria is satisfied
     */
    public int findPriceRange(long n, Money low, Money high) {
        TreeMap<Money, Integer> map = (TreeMap<Money, Integer>) setMap.get(n);
        int i = 0;
        for (Money e : map.keySet()) {
            if (e.compareTo(low) >= 0 && e.compareTo(high) <= 0) {
                i += map.get(e);
            }
        }
        return i;
    }


    /**
     * <p>Increases the price of products whose id is in the range specified by l and h, both inclusive by r%.</p>
     *
     * @param l lower limit of product's unique identifier
     * @param h upper limit of product's unique identifier
     * @param rate rate with which price is increased
     * @return cumulative price hike for all the products whose id's fall in the range specified by l and h.
     */
    public Money priceHike(long l, long h, double rate) {
        long hike = 0;
        while (l <= h) {
            if (entryMap.get(l) != null) {
                Money oldPrice = entryMap.get(l).price;
                long op = (oldPrice.dollars() * 100) + oldPrice.cents();
                long np = Double.valueOf(op * (1 + (0.01 * rate))).longValue();
                long dollars = np / 100;
                int cents = Long.valueOf(np % 100).intValue();
                Money newPrice = new Money(dollars, cents);
                insert(entryMap.get(l).id, newPrice, new ArrayList<>());
                hike += (np - op);
            }

            l++;
        }
        return new Money(hike / 100, Long.valueOf(hike % 100).intValue());
    }


    /**
     * <p>Remove elements of list from the description of a product, given it's id. It is possible that some of the items in the list are not in the
     * description of an intended product </p>
     *
     * @param id product's unique identifier
     * @param list list of items which needs to be removed from the description of an intended product
     * @return the sum of the numbers that are actually deleted from the description of a particular product.  Return 0 if there is no such id in the product catalog.
     */
    public long removeNames(long id, java.util.List<Long> list) {
        Entry e = entryMap.get(id);
        long sum = 0;
        for (Long x : list) {
            if (e.description.remove(x)) {
                sum += x;
                setMap.get(x).put(e.price, setMap.get(x).get(e.price) - 1);
                if (setMap.get(x).get(e.price) <= 0) setMap.get(x).remove(e.price);
                if (setMap.get(x).keySet().size() == 0) setMap.remove(x);
            }
        }
        return sum;
    }


    /**
     * <p> Money class represents the product's price in terms of dollars and cents</p>
     */
    public static class Money implements Comparable<Money> {
        long d;
        int c;


        /**
         * <p>Constructor for initializing dollar and cents to zero.</p>
         */
        public Money() {
            d = 0;
            c = 0;

        }

        /**
         * <p>Parameterized constructor for initializing dollars and cents.</p>
         *
         * @param d product's price specified by dollars
         * @param c product's price specified by cents
         */
        public Money(long d, int c) {
            this.d = d;
            this.c = c;

        }

        /**
         * <p>Parameterized constructor for initializing dollars and cents given a price in the form of string</p>
         *
         * @param s String representation of product's price
         */
        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if (len < 1) {
                d = 0;
                c = 0;
            } else if (part.length == 1) {
                d = Long.parseLong(s);
                c = 0;
            } else {
                d = Long.parseLong(part[0]);
                if (part[1].length() > 2) part[1] = part[1].substring(0, 2);
                c = Integer.parseInt(part[1]);
            }


        }

        /**
         * <p>Number of dollars in product's price</p>
         *
         * @return dollars associated with product's price
         */
        public long dollars() {
            return d;
        }

        /**
         * @return hash code of money object
         */
        @Override
        public int hashCode() {
            return Double.hashCode(Double.valueOf(this.toString()));
        }

        /**
         * <p>Number of Cents in product's price</p>
         *
         * @return cents associated with product's price
         */
        public int cents() {
            return c;
        }

        /**
         * <p>This function checks for the equality of two money objects</p>
         *
         * @param obj object to be checked for equality
         * @return true if objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            return this.d == ((Money) obj).d && this.c == ((Money) obj).c;
        }


        /**
         * <p>Compares the money object with another money object</p>
         *
         * @param other object to be compared with current money object
         * @return 1, if current money object is greater than other money object
         * 0, if current money object is equal to other money object
         * -1, if current money object is smaller than other money object
         */
        public int compareTo(Money other) {
            if (this.d == other.d && this.c == other.c) return 0;
            if (this.d == other.d) {
                if (this.c < other.c) return -1;
                return 1;
            }
            if (this.d < other.d) return -1;
            return 1;
        }

        /**
         * <p>String representation of the money object</p>
         *
         * @return the string representation of the money object
         */
        public String toString() {
            String str = String.valueOf(c);
            if (c < 10) str = "0" + str;
            return d + "." + str;
        }

    }

}
