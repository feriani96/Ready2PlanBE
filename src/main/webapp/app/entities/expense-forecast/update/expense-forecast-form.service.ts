import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IExpenseForecast, NewExpenseForecast } from '../expense-forecast.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExpenseForecast for edit and NewExpenseForecastFormGroupInput for create.
 */
type ExpenseForecastFormGroupInput = IExpenseForecast | PartialWithRequiredKeyOf<NewExpenseForecast>;

type ExpenseForecastFormDefaults = Pick<NewExpenseForecast, 'id'>;

type ExpenseForecastFormGroupContent = {
  id: FormControl<IExpenseForecast['id'] | NewExpenseForecast['id']>;
  label: FormControl<IExpenseForecast['label']>;
  monthlyAmount: FormControl<IExpenseForecast['monthlyAmount']>;
  forecast: FormControl<IExpenseForecast['forecast']>;
};

export type ExpenseForecastFormGroup = FormGroup<ExpenseForecastFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExpenseForecastFormService {
  createExpenseForecastFormGroup(expenseForecast: ExpenseForecastFormGroupInput = { id: null }): ExpenseForecastFormGroup {
    const expenseForecastRawValue = {
      ...this.getFormDefaults(),
      ...expenseForecast,
    };
    return new FormGroup<ExpenseForecastFormGroupContent>({
      id: new FormControl(
        { value: expenseForecastRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      label: new FormControl(expenseForecastRawValue.label),
      monthlyAmount: new FormControl(expenseForecastRawValue.monthlyAmount),
      forecast: new FormControl(expenseForecastRawValue.forecast),
    });
  }

  getExpenseForecast(form: ExpenseForecastFormGroup): IExpenseForecast | NewExpenseForecast {
    return form.getRawValue() as IExpenseForecast | NewExpenseForecast;
  }

  resetForm(form: ExpenseForecastFormGroup, expenseForecast: ExpenseForecastFormGroupInput): void {
    const expenseForecastRawValue = { ...this.getFormDefaults(), ...expenseForecast };
    form.reset(
      {
        ...expenseForecastRawValue,
        id: { value: expenseForecastRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ExpenseForecastFormDefaults {
    return {
      id: null,
    };
  }
}
