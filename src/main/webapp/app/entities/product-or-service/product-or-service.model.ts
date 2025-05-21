import { IBusinessPlan } from 'app/entities/business-plan/business-plan.model';

export interface IProductOrService {
  id: string;
  name?: string | null;
  description?: string | null;
  unitPrice?: number | null;
  estimatedMonthlySales?: number | null;
  durationInMonths?: number | null;
  businessPlan?: Pick<IBusinessPlan, 'id' | 'name'> | null;
}

export type NewProductOrService = Omit<IProductOrService, 'id'> & { id: null };
