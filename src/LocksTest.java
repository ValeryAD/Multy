import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LocksTest {
    public static void main(String[] args) {
        MyParking parking = new MyParking();
        Thread t1 = new Thread(new Supplier(parking), "supplier");
        Thread t2 = new Thread(new Consumer(parking), "Consumer");
        t1.start();
        t2.start();
    }
}


class MyParking {

    private final static int SPOTS_LIMIT = 3;
    List<Car> carSpots = new LinkedList<>();
    ReentrantLock lock = new ReentrantLock(false);
    Condition condition = lock.newCondition();


    public void setCar(Car car) {
        try {
            lock.lock();
            System.out.printf("%s comes to parking%n", car);
            System.out.printf("parking have %d free spots%n", SPOTS_LIMIT - carSpots.size());
            while (carSpots.size() >= SPOTS_LIMIT) {
                condition.await();
            }
            System.out.printf("%s took spot%n", car);
            carSpots.add(car);
            System.out.printf("parking have %d free spots%n", SPOTS_LIMIT - carSpots.size());
            condition.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void releaseCar() {
        try{
            lock.lock();
            System.out.println("check if any car can leave the parking");
            while(carSpots.size() <= 0){
                condition.await();
            }

            Car car = carSpots.stream().findAny().get();
            carSpots.remove(car);
            System.out.printf("%s leave the parking%n", car);
            System.out.printf("parking have %d free spots%n", SPOTS_LIMIT - carSpots.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            condition.signal();
            lock.unlock();
        }

    }

}

class Consumer implements Runnable {
    MyParking parking;

    public Consumer(MyParking parking) {
        this.parking = parking;
    }

    public void run() {
        for (int i = 0; i < 6; i++) {
            parking.releaseCar();
        }
    }
}


class Supplier implements Runnable {
    MyParking parking;

    public Supplier(MyParking parking) {
        this.parking = parking;
    }

    public void run() {
        for (int i = 0; i < 6; i++) {
            parking.setCar(new Car("Ford" + i));
        }

    }
}
