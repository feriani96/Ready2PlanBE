import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProductOrService } from '../product-or-service.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../product-or-service.test-samples';

import { ProductOrServiceService } from './product-or-service.service';

const requireRestSample: IProductOrService = {
  ...sampleWithRequiredData,
};

describe('ProductOrService Service', () => {
  let service: ProductOrServiceService;
  let httpMock: HttpTestingController;
  let expectedResult: IProductOrService | IProductOrService[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductOrServiceService);
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

    it('should create a ProductOrService', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const productOrService = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(productOrService).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductOrService', () => {
      const productOrService = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(productOrService).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductOrService', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductOrService', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProductOrService', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductOrServiceToCollectionIfMissing', () => {
      it('should add a ProductOrService to an empty array', () => {
        const productOrService: IProductOrService = sampleWithRequiredData;
        expectedResult = service.addProductOrServiceToCollectionIfMissing([], productOrService);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productOrService);
      });

      it('should not add a ProductOrService to an array that contains it', () => {
        const productOrService: IProductOrService = sampleWithRequiredData;
        const productOrServiceCollection: IProductOrService[] = [
          {
            ...productOrService,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductOrServiceToCollectionIfMissing(productOrServiceCollection, productOrService);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductOrService to an array that doesn't contain it", () => {
        const productOrService: IProductOrService = sampleWithRequiredData;
        const productOrServiceCollection: IProductOrService[] = [sampleWithPartialData];
        expectedResult = service.addProductOrServiceToCollectionIfMissing(productOrServiceCollection, productOrService);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productOrService);
      });

      it('should add only unique ProductOrService to an array', () => {
        const productOrServiceArray: IProductOrService[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const productOrServiceCollection: IProductOrService[] = [sampleWithRequiredData];
        expectedResult = service.addProductOrServiceToCollectionIfMissing(productOrServiceCollection, ...productOrServiceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productOrService: IProductOrService = sampleWithRequiredData;
        const productOrService2: IProductOrService = sampleWithPartialData;
        expectedResult = service.addProductOrServiceToCollectionIfMissing([], productOrService, productOrService2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productOrService);
        expect(expectedResult).toContain(productOrService2);
      });

      it('should accept null and undefined values', () => {
        const productOrService: IProductOrService = sampleWithRequiredData;
        expectedResult = service.addProductOrServiceToCollectionIfMissing([], null, productOrService, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productOrService);
      });

      it('should return initial array if no ProductOrService is added', () => {
        const productOrServiceCollection: IProductOrService[] = [sampleWithRequiredData];
        expectedResult = service.addProductOrServiceToCollectionIfMissing(productOrServiceCollection, undefined, null);
        expect(expectedResult).toEqual(productOrServiceCollection);
      });
    });

    describe('compareProductOrService', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProductOrService(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareProductOrService(entity1, entity2);
        const compareResult2 = service.compareProductOrService(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareProductOrService(entity1, entity2);
        const compareResult2 = service.compareProductOrService(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareProductOrService(entity1, entity2);
        const compareResult2 = service.compareProductOrService(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
