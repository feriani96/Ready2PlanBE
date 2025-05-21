import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRevenueForecast, NewRevenueForecast } from '../revenue-forecast.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRevenueForecast for edit and NewRevenueForecastFormGroupInput for create.
 */
type RevenueForecastFormGroupInput = IRevenueForecast | PartialWithRequiredKeyOf<NewRevenueForecast>;

type RevenueForecastFormDefaults = Pick<NewRevenueForecast, 'id'>;

type RevenueForecastFormGroupContent = {
  id: FormControl<IRevenueForecast['id'] | NewRevenueForecast['id']>;
  month: FormControl<IRevenueForecast['month']>;
  year: FormControl<IRevenueForecast['year']>;
  unitsSold: FormControl<IRevenueForecast['unitsSold']>;
  totalRevenue: FormControl<IRevenueForecast['totalRevenue']>;
  product: FormControl<IRevenueForecast['product']>;
  forecast: FormControl<IRevenueForecast['forecast']>;
};

export type RevenueForecastFormGroup = FormGroup<RevenueForecastFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RevenueForecastFormService {
  createRevenueForecastFormGroup(revenueForecast: RevenueForecastFormGroupInput = { id: null }): RevenueForecastFormGroup {
    const revenueForecastRawValue = {
      ...this.getFormDefaults(),
      ...revenueForecast,
    };
    return new FormGroup<RevenueForecastFormGroupContent>({
      id: new FormControl(
        { value: revenueForecastRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      month: new FormControl(revenueForecastRawValue.month),
      year: new FormControl(revenueForecastRawValue.year),
      unitsSold: new FormControl(revenueForecastRawValue.unitsSold),
      totalRevenue: new FormControl(revenueForecastRawValue.totalRevenue),
      product: new FormControl(revenueForecastRawValue.product),
      forecast: new FormControl(revenueForecastRawValue.forecast),
    });
  }

  getRevenueForecast(form: RevenueForecastFormGroup): IRevenueForecast | NewRevenueForecast {
    return form.getRawValue() as IRevenueForecast | NewRevenueForecast;
  }

  resetForm(form: RevenueForecastFormGroup, revenueForecast: RevenueForecastFormGroupInput): void {
    const revenueForecastRawValue = { ...this.getFormDefaults(), ...revenueForecast };
    form.reset(
      {
        ...revenueForecastRawValue,
        id: { value: revenueForecastRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RevenueForecastFormDefaults {
    return {
      id: null,
    };
  }
}
