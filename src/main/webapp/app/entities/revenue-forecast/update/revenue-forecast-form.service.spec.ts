import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../revenue-forecast.test-samples';

import { RevenueForecastFormService } from './revenue-forecast-form.service';

describe('RevenueForecast Form Service', () => {
  let service: RevenueForecastFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RevenueForecastFormService);
  });

  describe('Service methods', () => {
    describe('createRevenueForecastFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRevenueForecastFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            month: expect.any(Object),
            year: expect.any(Object),
            unitsSold: expect.any(Object),
            totalRevenue: expect.any(Object),
            product: expect.any(Object),
            forecast: expect.any(Object),
          })
        );
      });

      it('passing IRevenueForecast should create a new form with FormGroup', () => {
        const formGroup = service.createRevenueForecastFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            month: expect.any(Object),
            year: expect.any(Object),
            unitsSold: expect.any(Object),
            totalRevenue: expect.any(Object),
            product: expect.any(Object),
            forecast: expect.any(Object),
          })
        );
      });
    });

    describe('getRevenueForecast', () => {
      it('should return NewRevenueForecast for default RevenueForecast initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRevenueForecastFormGroup(sampleWithNewData);

        const revenueForecast = service.getRevenueForecast(formGroup) as any;

        expect(revenueForecast).toMatchObject(sampleWithNewData);
      });

      it('should return NewRevenueForecast for empty RevenueForecast initial value', () => {
        const formGroup = service.createRevenueForecastFormGroup();

        const revenueForecast = service.getRevenueForecast(formGroup) as any;

        expect(revenueForecast).toMatchObject({});
      });

      it('should return IRevenueForecast', () => {
        const formGroup = service.createRevenueForecastFormGroup(sampleWithRequiredData);

        const revenueForecast = service.getRevenueForecast(formGroup) as any;

        expect(revenueForecast).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRevenueForecast should not enable id FormControl', () => {
        const formGroup = service.createRevenueForecastFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRevenueForecast should disable id FormControl', () => {
        const formGroup = service.createRevenueForecastFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
