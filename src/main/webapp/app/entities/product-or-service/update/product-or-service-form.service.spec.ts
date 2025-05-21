import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-or-service.test-samples';

import { ProductOrServiceFormService } from './product-or-service-form.service';

describe('ProductOrService Form Service', () => {
  let service: ProductOrServiceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductOrServiceFormService);
  });

  describe('Service methods', () => {
    describe('createProductOrServiceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductOrServiceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            unitPrice: expect.any(Object),
            estimatedMonthlySales: expect.any(Object),
            durationInMonths: expect.any(Object),
            businessPlan: expect.any(Object),
          })
        );
      });

      it('passing IProductOrService should create a new form with FormGroup', () => {
        const formGroup = service.createProductOrServiceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            unitPrice: expect.any(Object),
            estimatedMonthlySales: expect.any(Object),
            durationInMonths: expect.any(Object),
            businessPlan: expect.any(Object),
          })
        );
      });
    });

    describe('getProductOrService', () => {
      it('should return NewProductOrService for default ProductOrService initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductOrServiceFormGroup(sampleWithNewData);

        const productOrService = service.getProductOrService(formGroup) as any;

        expect(productOrService).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductOrService for empty ProductOrService initial value', () => {
        const formGroup = service.createProductOrServiceFormGroup();

        const productOrService = service.getProductOrService(formGroup) as any;

        expect(productOrService).toMatchObject({});
      });

      it('should return IProductOrService', () => {
        const formGroup = service.createProductOrServiceFormGroup(sampleWithRequiredData);

        const productOrService = service.getProductOrService(formGroup) as any;

        expect(productOrService).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductOrService should not enable id FormControl', () => {
        const formGroup = service.createProductOrServiceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductOrService should disable id FormControl', () => {
        const formGroup = service.createProductOrServiceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
