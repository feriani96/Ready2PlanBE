import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../financial-forecast.test-samples';

import { FinancialForecastFormService } from './financial-forecast-form.service';

describe('FinancialForecast Form Service', () => {
  let service: FinancialForecastFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FinancialForecastFormService);
  });

  describe('Service methods', () => {
    describe('createFinancialForecastFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFinancialForecastFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            durationInMonths: expect.any(Object),
            businessPlan: expect.any(Object),
          })
        );
      });

      it('passing IFinancialForecast should create a new form with FormGroup', () => {
        const formGroup = service.createFinancialForecastFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            durationInMonths: expect.any(Object),
            businessPlan: expect.any(Object),
          })
        );
      });
    });

    describe('getFinancialForecast', () => {
      it('should return NewFinancialForecast for default FinancialForecast initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFinancialForecastFormGroup(sampleWithNewData);

        const financialForecast = service.getFinancialForecast(formGroup) as any;

        expect(financialForecast).toMatchObject(sampleWithNewData);
      });

      it('should return NewFinancialForecast for empty FinancialForecast initial value', () => {
        const formGroup = service.createFinancialForecastFormGroup();

        const financialForecast = service.getFinancialForecast(formGroup) as any;

        expect(financialForecast).toMatchObject({});
      });

      it('should return IFinancialForecast', () => {
        const formGroup = service.createFinancialForecastFormGroup(sampleWithRequiredData);

        const financialForecast = service.getFinancialForecast(formGroup) as any;

        expect(financialForecast).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFinancialForecast should not enable id FormControl', () => {
        const formGroup = service.createFinancialForecastFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFinancialForecast should disable id FormControl', () => {
        const formGroup = service.createFinancialForecastFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
