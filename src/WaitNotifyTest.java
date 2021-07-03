import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static void main(String[] args) {
        Parking parking = new Parking();
        Thread trIn = new Thread(new TrafficIn(parking), "trafficIn");
        Thread trOut = new Thread(new TrafficOut(parking),"trafficOut");
        trIn.setPriority(10);
        trIn.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        trOut.start();
    }
}

class Car {
    String model;

    public Car(String model) {
        this.model = model;
    }

    @Override
    public String toString(){
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model);
    }
}

class Parking {
    private final static int SPOTS_LIMIT = 3;
    List<Car> carSpots;

    public Parking() {
        carSpots = new LinkedList();
    }

    public synchronized void setCar(Car car) {
        System.out.printf("%s comes to parking%n", car);
        System.out.printf("parking have %d free spots%n", SPOTS_LIMIT - carSpots.size());
        while (carSpots.size() >= SPOTS_LIMIT) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("%s took spot%n", car);
        carSpots.add(car);
        System.out.printf("parking have %d free spots%n", SPOTS_LIMIT - carSpots.size());
        notify();
    }

    public synchronized Car releaseCar() {
        System.out.println("check if any car can leave the parking");
        while(carSpots.size() <= 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Car car = carSpots.stream().findAny().get();
        carSpots.remove(car);
        System.out.printf("%s leave the parking%n", car);
        System.out.printf("parking have %d free spots%n", SPOTS_LIMIT - carSpots.size());
        notify();
        return car;
    }
}

class TrafficOut implements Runnable {
    Parking parking;

    public TrafficOut(Parking parking) {
        this.parking = parking;
    }

    public void run() {
        for (int i = 0; i < 6; i++) {
            parking.releaseCar();
        }
    }
}


class TrafficIn implements Runnable {
    Parking parking;

    public TrafficIn(Parking parking) {
        this.parking = parking;
    }

    public void run() {
        for (int i = 0; i < 6; i++) {
            parking.setCar(new Car("Ford" + i));
        }

    }

}
