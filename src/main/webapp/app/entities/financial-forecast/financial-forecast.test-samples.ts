import { IFinancialForecast, NewFinancialForecast } from './financial-forecast.model';

export const sampleWithRequiredData: IFinancialForecast = {
  id: 'fced6e0b-eaad-4729-9639-7d7fcef87324',
};

export const sampleWithPartialData: IFinancialForecast = {
  id: '10fafa1c-05a4-40c0-b7ba-11549cfa3b46',
  startDate: 42039,
  durationInMonths: 68798,
};

export const sampleWithFullData: IFinancialForecast = {
  id: '4ae3969b-0747-41e2-860a-00ba7904b6e9',
  startDate: 27967,
  durationInMonths: 94225,
};

export const sampleWithNewData: NewFinancialForecast = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
