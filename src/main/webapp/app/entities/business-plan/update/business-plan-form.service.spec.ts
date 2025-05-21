import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../business-plan.test-samples';

import { BusinessPlanFormService } from './business-plan-form.service';

describe('BusinessPlan Form Service', () => {
  let service: BusinessPlanFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BusinessPlanFormService);
  });

  describe('Service methods', () => {
    describe('createBusinessPlanFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBusinessPlanFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            creationDate: expect.any(Object),
            entropreneurName: expect.any(Object),
          })
        );
      });

      it('passing IBusinessPlan should create a new form with FormGroup', () => {
        const formGroup = service.createBusinessPlanFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            creationDate: expect.any(Object),
            entropreneurName: expect.any(Object),
          })
        );
      });
    });

    describe('getBusinessPlan', () => {
      it('should return NewBusinessPlan for default BusinessPlan initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBusinessPlanFormGroup(sampleWithNewData);

        const businessPlan = service.getBusinessPlan(formGroup) as any;

        expect(businessPlan).toMatchObject(sampleWithNewData);
      });

      it('should return NewBusinessPlan for empty BusinessPlan initial value', () => {
        const formGroup = service.createBusinessPlanFormGroup();

        const businessPlan = service.getBusinessPlan(formGroup) as any;

        expect(businessPlan).toMatchObject({});
      });

      it('should return IBusinessPlan', () => {
        const formGroup = service.createBusinessPlanFormGroup(sampleWithRequiredData);

        const businessPlan = service.getBusinessPlan(formGroup) as any;

        expect(businessPlan).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBusinessPlan should not enable id FormControl', () => {
        const formGroup = service.createBusinessPlanFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBusinessPlan should disable id FormControl', () => {
        const formGroup = service.createBusinessPlanFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
