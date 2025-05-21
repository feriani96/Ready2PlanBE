import { IEntreprise, NewEntreprise } from './entreprise.model';

export const sampleWithRequiredData: IEntreprise = {
  id: '4133578d-0d0e-46bd-ae7d-4f22e5b3038e',
};

export const sampleWithPartialData: IEntreprise = {
  id: '613a0232-9ba4-42a7-a8c5-96eebfe90c09',
  pays: 'alarm Licensed Rand',
  telephone: 98457,
  description: 'holistic system Pound',
  devise: 'deposit communities',
};

export const sampleWithFullData: IEntreprise = {
  id: '2b3e2543-b59c-4dcc-8d52-e077ec77b7f2',
  nom_etp: 'Jewelery',
  pays: 'Music matrices',
  telephone: 49115,
  description: 'sexy',
  deviseSize: 86831,
  devise: 'JBOD quantifying',
};

export const sampleWithNewData: NewEntreprise = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
