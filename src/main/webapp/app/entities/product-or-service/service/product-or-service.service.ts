import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IProductOrService, NewProductOrService } from '../product-or-service.model';

export type PartialUpdateProductOrService = Partial<IProductOrService> & Pick<IProductOrService, 'id'>;

export type EntityResponseType = HttpResponse<IProductOrService>;
export type EntityArrayResponseType = HttpResponse<IProductOrService[]>;

@Injectable({ providedIn: 'root' })
export class ProductOrServiceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-or-services');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-or-services');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productOrService: NewProductOrService): Observable<EntityResponseType> {
    return this.http.post<IProductOrService>(this.resourceUrl, productOrService, { observe: 'response' });
  }

  update(productOrService: IProductOrService): Observable<EntityResponseType> {
    return this.http.put<IProductOrService>(
      `${this.resourceUrl}/${this.getProductOrServiceIdentifier(productOrService)}`,
      productOrService,
      { observe: 'response' }
    );
  }

  partialUpdate(productOrService: PartialUpdateProductOrService): Observable<EntityResponseType> {
    return this.http.patch<IProductOrService>(
      `${this.resourceUrl}/${this.getProductOrServiceIdentifier(productOrService)}`,
      productOrService,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IProductOrService>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductOrService[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductOrService[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductOrServiceIdentifier(productOrService: Pick<IProductOrService, 'id'>): string {
    return productOrService.id;
  }

  compareProductOrService(o1: Pick<IProductOrService, 'id'> | null, o2: Pick<IProductOrService, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductOrServiceIdentifier(o1) === this.getProductOrServiceIdentifier(o2) : o1 === o2;
  }

  addProductOrServiceToCollectionIfMissing<Type extends Pick<IProductOrService, 'id'>>(
    productOrServiceCollection: Type[],
    ...productOrServicesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productOrServices: Type[] = productOrServicesToCheck.filter(isPresent);
    if (productOrServices.length > 0) {
      const productOrServiceCollectionIdentifiers = productOrServiceCollection.map(
        productOrServiceItem => this.getProductOrServiceIdentifier(productOrServiceItem)!
      );
      const productOrServicesToAdd = productOrServices.filter(productOrServiceItem => {
        const productOrServiceIdentifier = this.getProductOrServiceIdentifier(productOrServiceItem);
        if (productOrServiceCollectionIdentifiers.includes(productOrServiceIdentifier)) {
          return false;
        }
        productOrServiceCollectionIdentifiers.push(productOrServiceIdentifier);
        return true;
      });
      return [...productOrServicesToAdd, ...productOrServiceCollection];
    }
    return productOrServiceCollection;
  }
}
