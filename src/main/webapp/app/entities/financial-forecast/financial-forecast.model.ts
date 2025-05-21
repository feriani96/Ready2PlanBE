import { IBusinessPlan } from 'app/entities/business-plan/business-plan.model';

export interface IFinancialForecast {
  id: string;
  startDate?: number | null;
  durationInMonths?: number | null;
  businessPlan?: Pick<IBusinessPlan, 'id'> | null;
}

export type NewFinancialForecast = Omit<IFinancialForecast, 'id'> & { id: null };
