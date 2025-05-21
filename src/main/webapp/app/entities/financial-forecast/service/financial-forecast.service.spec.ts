import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFinancialForecast } from '../financial-forecast.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../financial-forecast.test-samples';

import { FinancialForecastService } from './financial-forecast.service';

const requireRestSample: IFinancialForecast = {
  ...sampleWithRequiredData,
};

describe('FinancialForecast Service', () => {
  let service: FinancialForecastService;
  let httpMock: HttpTestingController;
  let expectedResult: IFinancialForecast | IFinancialForecast[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FinancialForecastService);
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

    it('should create a FinancialForecast', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const financialForecast = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(financialForecast).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FinancialForecast', () => {
      const financialForecast = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(financialForecast).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FinancialForecast', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FinancialForecast', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FinancialForecast', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFinancialForecastToCollectionIfMissing', () => {
      it('should add a FinancialForecast to an empty array', () => {
        const financialForecast: IFinancialForecast = sampleWithRequiredData;
        expectedResult = service.addFinancialForecastToCollectionIfMissing([], financialForecast);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(financialForecast);
      });

      it('should not add a FinancialForecast to an array that contains it', () => {
        const financialForecast: IFinancialForecast = sampleWithRequiredData;
        const financialForecastCollection: IFinancialForecast[] = [
          {
            ...financialForecast,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFinancialForecastToCollectionIfMissing(financialForecastCollection, financialForecast);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FinancialForecast to an array that doesn't contain it", () => {
        const financialForecast: IFinancialForecast = sampleWithRequiredData;
        const financialForecastCollection: IFinancialForecast[] = [sampleWithPartialData];
        expectedResult = service.addFinancialForecastToCollectionIfMissing(financialForecastCollection, financialForecast);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(financialForecast);
      });

      it('should add only unique FinancialForecast to an array', () => {
        const financialForecastArray: IFinancialForecast[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const financialForecastCollection: IFinancialForecast[] = [sampleWithRequiredData];
        expectedResult = service.addFinancialForecastToCollectionIfMissing(financialForecastCollection, ...financialForecastArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const financialForecast: IFinancialForecast = sampleWithRequiredData;
        const financialForecast2: IFinancialForecast = sampleWithPartialData;
        expectedResult = service.addFinancialForecastToCollectionIfMissing([], financialForecast, financialForecast2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(financialForecast);
        expect(expectedResult).toContain(financialForecast2);
      });

      it('should accept null and undefined values', () => {
        const financialForecast: IFinancialForecast = sampleWithRequiredData;
        expectedResult = service.addFinancialForecastToCollectionIfMissing([], null, financialForecast, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(financialForecast);
      });

      it('should return initial array if no FinancialForecast is added', () => {
        const financialForecastCollection: IFinancialForecast[] = [sampleWithRequiredData];
        expectedResult = service.addFinancialForecastToCollectionIfMissing(financialForecastCollection, undefined, null);
        expect(expectedResult).toEqual(financialForecastCollection);
      });
    });

    describe('compareFinancialForecast', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFinancialForecast(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareFinancialForecast(entity1, entity2);
        const compareResult2 = service.compareFinancialForecast(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareFinancialForecast(entity1, entity2);
        const compareResult2 = service.compareFinancialForecast(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareFinancialForecast(entity1, entity2);
        const compareResult2 = service.compareFinancialForecast(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
