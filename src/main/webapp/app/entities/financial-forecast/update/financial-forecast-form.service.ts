import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFinancialForecast, NewFinancialForecast } from '../financial-forecast.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFinancialForecast for edit and NewFinancialForecastFormGroupInput for create.
 */
type FinancialForecastFormGroupInput = IFinancialForecast | PartialWithRequiredKeyOf<NewFinancialForecast>;

type FinancialForecastFormDefaults = Pick<NewFinancialForecast, 'id'>;

type FinancialForecastFormGroupContent = {
  id: FormControl<IFinancialForecast['id'] | NewFinancialForecast['id']>;
  startDate: FormControl<IFinancialForecast['startDate']>;
  durationInMonths: FormControl<IFinancialForecast['durationInMonths']>;
  businessPlan: FormControl<IFinancialForecast['businessPlan']>;
};

export type FinancialForecastFormGroup = FormGroup<FinancialForecastFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FinancialForecastFormService {
  createFinancialForecastFormGroup(financialForecast: FinancialForecastFormGroupInput = { id: null }): FinancialForecastFormGroup {
    const financialForecastRawValue = {
      ...this.getFormDefaults(),
      ...financialForecast,
    };
    return new FormGroup<FinancialForecastFormGroupContent>({
      id: new FormControl(
        { value: financialForecastRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      startDate: new FormControl(financialForecastRawValue.startDate),
      durationInMonths: new FormControl(financialForecastRawValue.durationInMonths),
      businessPlan: new FormControl(financialForecastRawValue.businessPlan),
    });
  }

  getFinancialForecast(form: FinancialForecastFormGroup): IFinancialForecast | NewFinancialForecast {
    return form.getRawValue() as IFinancialForecast | NewFinancialForecast;
  }

  resetForm(form: FinancialForecastFormGroup, financialForecast: FinancialForecastFormGroupInput): void {
    const financialForecastRawValue = { ...this.getFormDefaults(), ...financialForecast };
    form.reset(
      {
        ...financialForecastRawValue,
        id: { value: financialForecastRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FinancialForecastFormDefaults {
    return {
      id: null,
    };
  }
}
