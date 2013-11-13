package rental;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rental.Car;
import rental.CarType;

@Generated(value="EclipseLink-2.5.0.v20130507-rNA", date="2013-11-13T22:41:17")
@StaticMetamodel(CarRentalCompany.class)
public class CarRentalCompany_ { 

    public static volatile ListAttribute<CarRentalCompany, Car> cars;
    public static volatile SingularAttribute<CarRentalCompany, String> name;
    public static volatile SetAttribute<CarRentalCompany, CarType> carTypes;

}