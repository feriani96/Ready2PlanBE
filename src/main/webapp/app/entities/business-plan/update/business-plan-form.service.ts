import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBusinessPlan, NewBusinessPlan } from '../business-plan.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBusinessPlan for edit and NewBusinessPlanFormGroupInput for create.
 */
type BusinessPlanFormGroupInput = IBusinessPlan | PartialWithRequiredKeyOf<NewBusinessPlan>;

type BusinessPlanFormDefaults = Pick<NewBusinessPlan, 'id'>;

type BusinessPlanFormGroupContent = {
  id: FormControl<IBusinessPlan['id'] | NewBusinessPlan['id']>;
  name: FormControl<IBusinessPlan['name']>;
  description: FormControl<IBusinessPlan['description']>;
  creationDate: FormControl<IBusinessPlan['creationDate']>;
  entropreneurName: FormControl<IBusinessPlan['entropreneurName']>;
};

export type BusinessPlanFormGroup = FormGroup<BusinessPlanFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BusinessPlanFormService {
  createBusinessPlanFormGroup(businessPlan: BusinessPlanFormGroupInput = { id: null }): BusinessPlanFormGroup {
    const businessPlanRawValue = {
      ...this.getFormDefaults(),
      ...businessPlan,
    };
    return new FormGroup<BusinessPlanFormGroupContent>({
      id: new FormControl(
        { value: businessPlanRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(businessPlanRawValue.name),
      description: new FormControl(businessPlanRawValue.description),
      creationDate: new FormControl(businessPlanRawValue.creationDate),
      entropreneurName: new FormControl(businessPlanRawValue.entropreneurName),
    });
  }

  getBusinessPlan(form: BusinessPlanFormGroup): IBusinessPlan | NewBusinessPlan {
    return form.getRawValue() as IBusinessPlan | NewBusinessPlan;
  }

  resetForm(form: BusinessPlanFormGroup, businessPlan: BusinessPlanFormGroupInput): void {
    const businessPlanRawValue = { ...this.getFormDefaults(), ...businessPlan };
    form.reset(
      {
        ...businessPlanRawValue,
        id: { value: businessPlanRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BusinessPlanFormDefaults {
    return {
      id: null,
    };
  }
}
