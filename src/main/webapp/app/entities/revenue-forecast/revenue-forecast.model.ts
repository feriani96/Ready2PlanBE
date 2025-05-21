import { IProductOrService } from 'app/entities/product-or-service/product-or-service.model';
import { IFinancialForecast } from 'app/entities/financial-forecast/financial-forecast.model';

export interface IRevenueForecast {
  id: string;
  month?: number | null;
  year?: number | null;
  unitsSold?: number | null;
  totalRevenue?: number | null;
  product?: Pick<IProductOrService, 'id' | 'name'> | null;
  forecast?: Pick<IFinancialForecast, 'id' | 'startDate'> | null;
}

export type NewRevenueForecast = Omit<IRevenueForecast, 'id'> & { id: null };
