import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRevenueForecast } from '../revenue-forecast.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../revenue-forecast.test-samples';

import { RevenueForecastService } from './revenue-forecast.service';

const requireRestSample: IRevenueForecast = {
  ...sampleWithRequiredData,
};

describe('RevenueForecast Service', () => {
  let service: RevenueForecastService;
  let httpMock: HttpTestingController;
  let expectedResult: IRevenueForecast | IRevenueForecast[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RevenueForecastService);
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

    it('should create a RevenueForecast', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const revenueForecast = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(revenueForecast).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RevenueForecast', () => {
      const revenueForecast = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(revenueForecast).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RevenueForecast', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RevenueForecast', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RevenueForecast', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRevenueForecastToCollectionIfMissing', () => {
      it('should add a RevenueForecast to an empty array', () => {
        const revenueForecast: IRevenueForecast = sampleWithRequiredData;
        expectedResult = service.addRevenueForecastToCollectionIfMissing([], revenueForecast);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(revenueForecast);
      });

      it('should not add a RevenueForecast to an array that contains it', () => {
        const revenueForecast: IRevenueForecast = sampleWithRequiredData;
        const revenueForecastCollection: IRevenueForecast[] = [
          {
            ...revenueForecast,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRevenueForecastToCollectionIfMissing(revenueForecastCollection, revenueForecast);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RevenueForecast to an array that doesn't contain it", () => {
        const revenueForecast: IRevenueForecast = sampleWithRequiredData;
        const revenueForecastCollection: IRevenueForecast[] = [sampleWithPartialData];
        expectedResult = service.addRevenueForecastToCollectionIfMissing(revenueForecastCollection, revenueForecast);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(revenueForecast);
      });

      it('should add only unique RevenueForecast to an array', () => {
        const revenueForecastArray: IRevenueForecast[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const revenueForecastCollection: IRevenueForecast[] = [sampleWithRequiredData];
        expectedResult = service.addRevenueForecastToCollectionIfMissing(revenueForecastCollection, ...revenueForecastArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const revenueForecast: IRevenueForecast = sampleWithRequiredData;
        const revenueForecast2: IRevenueForecast = sampleWithPartialData;
        expectedResult = service.addRevenueForecastToCollectionIfMissing([], revenueForecast, revenueForecast2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(revenueForecast);
        expect(expectedResult).toContain(revenueForecast2);
      });

      it('should accept null and undefined values', () => {
        const revenueForecast: IRevenueForecast = sampleWithRequiredData;
        expectedResult = service.addRevenueForecastToCollectionIfMissing([], null, revenueForecast, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(revenueForecast);
      });

      it('should return initial array if no RevenueForecast is added', () => {
        const revenueForecastCollection: IRevenueForecast[] = [sampleWithRequiredData];
        expectedResult = service.addRevenueForecastToCollectionIfMissing(revenueForecastCollection, undefined, null);
        expect(expectedResult).toEqual(revenueForecastCollection);
      });
    });

    describe('compareRevenueForecast', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRevenueForecast(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareRevenueForecast(entity1, entity2);
        const compareResult2 = service.compareRevenueForecast(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareRevenueForecast(entity1, entity2);
        const compareResult2 = service.compareRevenueForecast(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareRevenueForecast(entity1, entity2);
        const compareResult2 = service.compareRevenueForecast(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
