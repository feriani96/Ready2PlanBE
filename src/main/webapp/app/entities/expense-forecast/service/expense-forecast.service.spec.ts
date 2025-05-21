import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IExpenseForecast } from '../expense-forecast.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../expense-forecast.test-samples';

import { ExpenseForecastService } from './expense-forecast.service';

const requireRestSample: IExpenseForecast = {
  ...sampleWithRequiredData,
};

describe('ExpenseForecast Service', () => {
  let service: ExpenseForecastService;
  let httpMock: HttpTestingController;
  let expectedResult: IExpenseForecast | IExpenseForecast[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ExpenseForecastService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ExpenseForecast', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const expenseForecast = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(expenseForecast).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ExpenseForecast', () => {
      const expenseForecast = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(expenseForecast).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ExpenseForecast', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ExpenseForecast', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ExpenseForecast', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addExpenseForecastToCollectionIfMissing', () => {
      it('should add a ExpenseForecast to an empty array', () => {
        const expenseForecast: IExpenseForecast = sampleWithRequiredData;
        expectedResult = service.addExpenseForecastToCollectionIfMissing([], expenseForecast);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(expenseForecast);
      });

      it('should not add a ExpenseForecast to an array that contains it', () => {
        const expenseForecast: IExpenseForecast = sampleWithRequiredData;
        const expenseForecastCollection: IExpenseForecast[] = [
          {
            ...expenseForecast,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addExpenseForecastToCollectionIfMissing(expenseForecastCollection, expenseForecast);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ExpenseForecast to an array that doesn't contain it", () => {
        const expenseForecast: IExpenseForecast = sampleWithRequiredData;
        const expenseForecastCollection: IExpenseForecast[] = [sampleWithPartialData];
        expectedResult = service.addExpenseForecastToCollectionIfMissing(expenseForecastCollection, expenseForecast);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(expenseForecast);
      });

      it('should add only unique ExpenseForecast to an array', () => {
        const expenseForecastArray: IExpenseForecast[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const expenseForecastCollection: IExpenseForecast[] = [sampleWithRequiredData];
        expectedResult = service.addExpenseForecastToCollectionIfMissing(expenseForecastCollection, ...expenseForecastArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const expenseForecast: IExpenseForecast = sampleWithRequiredData;
        const expenseForecast2: IExpenseForecast = sampleWithPartialData;
        expectedResult = service.addExpenseForecastToCollectionIfMissing([], expenseForecast, expenseForecast2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(expenseForecast);
        expect(expectedResult).toContain(expenseForecast2);
      });

      it('should accept null and undefined values', () => {
        const expenseForecast: IExpenseForecast = sampleWithRequiredData;
        expectedResult = service.addExpenseForecastToCollectionIfMissing([], null, expenseForecast, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(expenseForecast);
      });

      it('should return initial array if no ExpenseForecast is added', () => {
        const expenseForecastCollection: IExpenseForecast[] = [sampleWithRequiredData];
        expectedResult = service.addExpenseForecastToCollectionIfMissing(expenseForecastCollection, undefined, null);
        expect(expectedResult).toEqual(expenseForecastCollection);
      });
    });

    describe('compareExpenseForecast', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareExpenseForecast(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareExpenseForecast(entity1, entity2);
        const compareResult2 = service.compareExpenseForecast(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareExpenseForecast(entity1, entity2);
        const compareResult2 = service.compareExpenseForecast(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareExpenseForecast(entity1, entity2);
        const compareResult2 = service.compareExpenseForecast(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
