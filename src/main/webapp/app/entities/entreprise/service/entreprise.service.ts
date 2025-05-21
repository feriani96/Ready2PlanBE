import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IEntreprise, NewEntreprise } from '../entreprise.model';

export type PartialUpdateEntreprise = Partial<IEntreprise> & Pick<IEntreprise, 'id'>;

export type EntityResponseType = HttpResponse<IEntreprise>;
export type EntityArrayResponseType = HttpResponse<IEntreprise[]>;

@Injectable({ providedIn: 'root' })
export class EntrepriseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/entreprises');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/entreprises');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(entreprise: NewEntreprise): Observable<EntityResponseType> {
    return this.http.post<IEntreprise>(this.resourceUrl, entreprise, { observe: 'response' });
  }

  update(entreprise: IEntreprise): Observable<EntityResponseType> {
    return this.http.put<IEntreprise>(`${this.resourceUrl}/${this.getEntrepriseIdentifier(entreprise)}`, entreprise, {
      observe: 'response',
    });
  }

  partialUpdate(entreprise: PartialUpdateEntreprise): Observable<EntityResponseType> {
    return this.http.patch<IEntreprise>(`${this.resourceUrl}/${this.getEntrepriseIdentifier(entreprise)}`, entreprise, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IEntreprise>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEntreprise[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEntreprise[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getEntrepriseIdentifier(entreprise: Pick<IEntreprise, 'id'>): string {
    return entreprise.id;
  }

  compareEntreprise(o1: Pick<IEntreprise, 'id'> | null, o2: Pick<IEntreprise, 'id'> | null): boolean {
    return o1 && o2 ? this.getEntrepriseIdentifier(o1) === this.getEntrepriseIdentifier(o2) : o1 === o2;
  }

  addEntrepriseToCollectionIfMissing<Type extends Pick<IEntreprise, 'id'>>(
    entrepriseCollection: Type[],
    ...entreprisesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const entreprises: Type[] = entreprisesToCheck.filter(isPresent);
    if (entreprises.length > 0) {
      const entrepriseCollectionIdentifiers = entrepriseCollection.map(entrepriseItem => this.getEntrepriseIdentifier(entrepriseItem)!);
      const entreprisesToAdd = entreprises.filter(entrepriseItem => {
        const entrepriseIdentifier = this.getEntrepriseIdentifier(entrepriseItem);
        if (entrepriseCollectionIdentifiers.includes(entrepriseIdentifier)) {
          return false;
        }
        entrepriseCollectionIdentifiers.push(entrepriseIdentifier);
        return true;
      });
      return [...entreprisesToAdd, ...entrepriseCollection];
    }
    return entrepriseCollection;
  }
}
