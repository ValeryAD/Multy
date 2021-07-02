import java.util.LinkedList;
import java.util.List;

public class Runner {
    public static void main(String[] args) {

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
}

class Parking {
    private final static int SPOTS_LIMIT = 3;
    List<Car> carSpots;

    public Parking() {
        carSpots = new LinkedList();
    }

    public synchronized void setCar(Car car) {
        System.out.printf("%s comes to parking%n", car);
        System.out.printf("");
        while (carSpots.size() >= SPOTS_LIMIT) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("%s took spot%n", car);
        carSpots.add(car);
        notify();
    }

    public synchronized Car releaseCar() {
        Car car = carSpots.stream().findAny().get();
        System.out.printf("%s comes to parking%n", car);
        while(carSpots.size() <= 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("%s took spot%n", car);
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
        for (int i = 0; i <= 6; i++) {
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
