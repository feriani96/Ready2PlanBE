import { IProductOrService, NewProductOrService } from './product-or-service.model';

export const sampleWithRequiredData: IProductOrService = {
  id: '8f1a263e-f631-4bcc-9fde-0d7f0ddd4d1e',
};

export const sampleWithPartialData: IProductOrService = {
  id: 'b595e0b2-c57e-4405-91fe-bdfbed3bb492',
  name: 'Fresh',
  unitPrice: 87660,
  durationInMonths: 19710,
};

export const sampleWithFullData: IProductOrService = {
  id: 'ddf6cd86-1d5d-4a89-b278-6c51de7bf943',
  name: 'Intelligent Tactics Savings',
  description: 'needs-based',
  unitPrice: 61605,
  estimatedMonthlySales: 97154,
  durationInMonths: 29013,
};

export const sampleWithNewData: NewProductOrService = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
