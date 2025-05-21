import { IFinancialForecast } from 'app/entities/financial-forecast/financial-forecast.model';

export interface IExpenseForecast {
  id: string;
  label?: string | null;
  monthlyAmount?: number | null;
  forecast?: Pick<IFinancialForecast, 'id' | 'startDate'> | null;
}

export type NewExpenseForecast = Omit<IExpenseForecast, 'id'> & { id: null };
