package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.coordinator.CarCoordinator;
import main.dto.Car;
import main.dto.RedCar;
import main.service.CarService;
import mockit.Mock;
import mockit.MockUp;

class CarCoordinatorUnitTest {

	CarCoordinator carCoordinator;

	@BeforeEach
	void test() {
		carCoordinator = new CarCoordinator();  
	}

	@Test
	public void isOpportunityAvailable() { // Servisten gelen en uygun f�rsat�n bulunarak listelendi�i senaryo.

		new MockUp<CarService>() { 
			@Mock
			public Car[] getSource() { 
				Car car1 = new Car();
				car1.setPrice(300000);
				Car car2 = new Car();
				car2.setPrice(200000);
				return new Car[] { car1, car2 };  
			}
		};

		// Car mockCar = mock(Car.class);
		// doReturn(100000).when(mockCar).getPrice();

		Boolean result = carCoordinator.isOpportunity(); 

		// verify(mockCar).getPrice();
		assertNotNull(result);
		assertTrue(result); 

	} 

	@Test
	public void isOpportunityWhenServiceIsEmpty() { // Servis bo� liste d�nerse metodun davran���, Null kontrol� var m�?

		new MockUp<CarService>() {

			@Mock
			public Car[] getSource() {
				return new Car[] {};
			}
		};

		// CarService mockCarService = mock(CarService.class);
		// doReturn(new Car[] {}).when(mockCarService.getSource());

		Boolean result = carCoordinator.isOpportunity();

		// verify(mockCarService).getSource();
		assertNotNull(result);
		assertFalse(result);
	}

	@Test
	public void isOpportunityWhenServiceIsNull() { // Servisin Null d�nd��� senaryo, Null kontrol� var m�?

		new MockUp<CarService>() {

			@Mock
			public Car[] getSource() {
				return null;
			}
		};

		Boolean result = carCoordinator.isOpportunity();

		assertNotNull(result);
		assertFalse(result);
	}

	@Test
	public void isOpportunityWhenCarCollectionIsEmpty() { // Koleksiyon asl�nda Bo� Gelmemeli,
															// zaten servis parametresi kontrol edilmi� olmal�.

		new MockUp<CarCoordinator>() {

			@Mock
			public Map<Integer, Car> collectCarData(Car[] c) {
				return new HashMap<Integer, Car>();
			}
		};

		Boolean result = carCoordinator.isOpportunity();
		assertNotNull(result);
		assertTrue(result); 
	}

	@Test
	public void isOpportunityWhenCarCollectionIsNull() { // Asl�nda koleksiyon Null olmamal�,
															// ��nk� servistan gelen veri zaten kontrol edilmi� olmal�.
															// Yine de bu durum ger�ekle�irse NP Exception vermeli.

		new MockUp<CarCoordinator>() {

			@Mock
			public Map<Integer, Car> collectCarData(Car[] c) {
				return null;
			}
		};

		assertThrows(NullPointerException.class,

				() -> carCoordinator.isOpportunity());

	}

	@Test
	public void isOpportunityWhenPriceIsOutOfBudget() { // En uygun f�rsat�n b�t�eyi a�t��� senaryo

		new MockUp<Car>() {

			@Mock
			public int getPrice() {
				return 300000;
			}
		};

		Boolean result = carCoordinator.isOpportunity();

		assertNotNull(result);
		assertFalse(result);

	}

	@Test
	public void collectCarDataUnitTest() { // Servisten dolu gelen listenin koleksiyona aktar�lmas�.

		Map<Integer, Car> result = carCoordinator.collectCarData(new Car[] { new Car(), new RedCar() });

		assertNotNull(result);
		assertNotNull(result.get(new Integer(0)).getPrice());
	}

	@Test
	public void collectCarDataWithEmptyList() { // Servisten bo� liste parametresi d�nmesi
		Map<Integer, Car> carCollection = carCoordinator.collectCarData(new Car[] {});
		assertNotNull(carCollection);
		assertTrue(carCollection.isEmpty()); 
	}

	@Test
	public void collectCarDataWithNullParameter() { // Null gelen servis parametresinin hataya sebep olmas�.
		assertThrows(NullPointerException.class, () -> carCoordinator.collectCarData(null));

	}

	@Test
	public void sortCarPricesUnitTestWithNonEmptyCarList() { // Dolu ara� listesinden gelen fiyatlar�n s�ralanmas�
		Car car1 = new Car();
		car1.setPrice(200000);
		Car car2 = new Car();
		car2.setPrice(100000);
		List<Car> result = CarCoordinator.sortCarPrices(new Car[] { car1, car2 });

		assertNotNull(result);
		assertNotNull(result.get(0));
		assertNotNull(result.get(0).getPrice());
	}

	@Test
	public void sortCarPricesWithEmptyCarList() { // Ara� listesi bo� oldu�unda s�ralama metodunun davran���
		List<Car> result = CarCoordinator.sortCarPrices(new Car[] {});

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void sortCarPricesWithNullParameter() { // Bo� ara� listesinin s�ralama metodunda hataya sebep olmas�
		assertThrows(NullPointerException.class,

				() -> CarCoordinator.sortCarPrices(null));
	}

	@Test
	public void sortCarPricesWhenPricesAreEqual() { // Listede e�it fiyatl� ara�lar oldu�unda
		Car[] carArray = new Car[2];

		for (int i = 0; i < 2; i++) {
			Car car = new Car();
			car.setPrice(100000);
			carArray[i] = car;
		}

		List<Car> result = CarCoordinator.sortCarPrices(carArray);
		assertNotNull(result);
		assertNotNull(result.get(0));
		assertNotNull(result.get(0).getPrice());
		assertEquals(result.get(0).getPrice(), result.get(1).getPrice());
	}

	@Test
	public void priceDiffUnitTest() { // Rastgele fiyat fark� �reten methodun
		double result = carCoordinator.priceDiff();
		assertNotNull(result);
	}

	@Test
	public void getBestOfferUnitTest() { // Servis parametresi dolu geldi�inde en uygun f�rsat�n hesaplanmas�
		Car car1 = new Car();
		car1.setPrice(200000);
		Car car2 = new Car();
		car2.setPrice(300000);

		Car[] carList = new Car[] { car1, car2 };
		Car result = carCoordinator.getBestOffer(carList);
		assertNotNull(result);
	}

	@Test
	public void getBestOfferWithEmptyList() { // Servis parametresi bo� geldi�inde en uygun f�rsat hesaplanamaz

		assertThrows(IndexOutOfBoundsException.class,

				() -> carCoordinator.getBestOffer(new Car[] {}));

	}

	@Test
	public void getBestOfferWithNullList() { // Servis parametresi null geldi�inde en uygun f�rsat hesaplanamaz

		assertThrows(NullPointerException.class,

				() -> carCoordinator.getBestOffer(null));
	}
}
