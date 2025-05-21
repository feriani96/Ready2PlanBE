import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../expense-forecast.test-samples';

import { ExpenseForecastFormService } from './expense-forecast-form.service';

describe('ExpenseForecast Form Service', () => {
  let service: ExpenseForecastFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExpenseForecastFormService);
  });

  describe('Service methods', () => {
    describe('createExpenseForecastFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExpenseForecastFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            monthlyAmount: expect.any(Object),
            forecast: expect.any(Object),
          })
        );
      });

      it('passing IExpenseForecast should create a new form with FormGroup', () => {
        const formGroup = service.createExpenseForecastFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            monthlyAmount: expect.any(Object),
            forecast: expect.any(Object),
          })
        );
      });
    });

    describe('getExpenseForecast', () => {
      it('should return NewExpenseForecast for default ExpenseForecast initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createExpenseForecastFormGroup(sampleWithNewData);

        const expenseForecast = service.getExpenseForecast(formGroup) as any;

        expect(expenseForecast).toMatchObject(sampleWithNewData);
      });

      it('should return NewExpenseForecast for empty ExpenseForecast initial value', () => {
        const formGroup = service.createExpenseForecastFormGroup();

        const expenseForecast = service.getExpenseForecast(formGroup) as any;

        expect(expenseForecast).toMatchObject({});
      });

      it('should return IExpenseForecast', () => {
        const formGroup = service.createExpenseForecastFormGroup(sampleWithRequiredData);

        const expenseForecast = service.getExpenseForecast(formGroup) as any;

        expect(expenseForecast).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExpenseForecast should not enable id FormControl', () => {
        const formGroup = service.createExpenseForecastFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExpenseForecast should disable id FormControl', () => {
        const formGroup = service.createExpenseForecastFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
