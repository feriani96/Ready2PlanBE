import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBusinessPlan } from '../business-plan.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../business-plan.test-samples';

import { BusinessPlanService } from './business-plan.service';

const requireRestSample: IBusinessPlan = {
  ...sampleWithRequiredData,
};

describe('BusinessPlan Service', () => {
  let service: BusinessPlanService;
  let httpMock: HttpTestingController;
  let expectedResult: IBusinessPlan | IBusinessPlan[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BusinessPlanService);
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

    it('should create a BusinessPlan', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const businessPlan = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(businessPlan).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BusinessPlan', () => {
      const businessPlan = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(businessPlan).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BusinessPlan', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BusinessPlan', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BusinessPlan', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBusinessPlanToCollectionIfMissing', () => {
      it('should add a BusinessPlan to an empty array', () => {
        const businessPlan: IBusinessPlan = sampleWithRequiredData;
        expectedResult = service.addBusinessPlanToCollectionIfMissing([], businessPlan);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(businessPlan);
      });

      it('should not add a BusinessPlan to an array that contains it', () => {
        const businessPlan: IBusinessPlan = sampleWithRequiredData;
        const businessPlanCollection: IBusinessPlan[] = [
          {
            ...businessPlan,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBusinessPlanToCollectionIfMissing(businessPlanCollection, businessPlan);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BusinessPlan to an array that doesn't contain it", () => {
        const businessPlan: IBusinessPlan = sampleWithRequiredData;
        const businessPlanCollection: IBusinessPlan[] = [sampleWithPartialData];
        expectedResult = service.addBusinessPlanToCollectionIfMissing(businessPlanCollection, businessPlan);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(businessPlan);
      });

      it('should add only unique BusinessPlan to an array', () => {
        const businessPlanArray: IBusinessPlan[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const businessPlanCollection: IBusinessPlan[] = [sampleWithRequiredData];
        expectedResult = service.addBusinessPlanToCollectionIfMissing(businessPlanCollection, ...businessPlanArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const businessPlan: IBusinessPlan = sampleWithRequiredData;
        const businessPlan2: IBusinessPlan = sampleWithPartialData;
        expectedResult = service.addBusinessPlanToCollectionIfMissing([], businessPlan, businessPlan2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(businessPlan);
        expect(expectedResult).toContain(businessPlan2);
      });

      it('should accept null and undefined values', () => {
        const businessPlan: IBusinessPlan = sampleWithRequiredData;
        expectedResult = service.addBusinessPlanToCollectionIfMissing([], null, businessPlan, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(businessPlan);
      });

      it('should return initial array if no BusinessPlan is added', () => {
        const businessPlanCollection: IBusinessPlan[] = [sampleWithRequiredData];
        expectedResult = service.addBusinessPlanToCollectionIfMissing(businessPlanCollection, undefined, null);
        expect(expectedResult).toEqual(businessPlanCollection);
      });
    });

    describe('compareBusinessPlan', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBusinessPlan(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareBusinessPlan(entity1, entity2);
        const compareResult2 = service.compareBusinessPlan(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareBusinessPlan(entity1, entity2);
        const compareResult2 = service.compareBusinessPlan(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareBusinessPlan(entity1, entity2);
        const compareResult2 = service.compareBusinessPlan(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
