import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductOrService, NewProductOrService } from '../product-or-service.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductOrService for edit and NewProductOrServiceFormGroupInput for create.
 */
type ProductOrServiceFormGroupInput = IProductOrService | PartialWithRequiredKeyOf<NewProductOrService>;

type ProductOrServiceFormDefaults = Pick<NewProductOrService, 'id'>;

type ProductOrServiceFormGroupContent = {
  id: FormControl<IProductOrService['id'] | NewProductOrService['id']>;
  name: FormControl<IProductOrService['name']>;
  description: FormControl<IProductOrService['description']>;
  unitPrice: FormControl<IProductOrService['unitPrice']>;
  estimatedMonthlySales: FormControl<IProductOrService['estimatedMonthlySales']>;
  durationInMonths: FormControl<IProductOrService['durationInMonths']>;
  businessPlan: FormControl<IProductOrService['businessPlan']>;
};

export type ProductOrServiceFormGroup = FormGroup<ProductOrServiceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductOrServiceFormService {
  createProductOrServiceFormGroup(productOrService: ProductOrServiceFormGroupInput = { id: null }): ProductOrServiceFormGroup {
    const productOrServiceRawValue = {
      ...this.getFormDefaults(),
      ...productOrService,
    };
    return new FormGroup<ProductOrServiceFormGroupContent>({
      id: new FormControl(
        { value: productOrServiceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(productOrServiceRawValue.name),
      description: new FormControl(productOrServiceRawValue.description),
      unitPrice: new FormControl(productOrServiceRawValue.unitPrice),
      estimatedMonthlySales: new FormControl(productOrServiceRawValue.estimatedMonthlySales),
      durationInMonths: new FormControl(productOrServiceRawValue.durationInMonths),
      businessPlan: new FormControl(productOrServiceRawValue.businessPlan),
    });
  }

  getProductOrService(form: ProductOrServiceFormGroup): IProductOrService | NewProductOrService {
    return form.getRawValue() as IProductOrService | NewProductOrService;
  }

  resetForm(form: ProductOrServiceFormGroup, productOrService: ProductOrServiceFormGroupInput): void {
    const productOrServiceRawValue = { ...this.getFormDefaults(), ...productOrService };
    form.reset(
      {
        ...productOrServiceRawValue,
        id: { value: productOrServiceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductOrServiceFormDefaults {
    return {
      id: null,
    };
  }
}
