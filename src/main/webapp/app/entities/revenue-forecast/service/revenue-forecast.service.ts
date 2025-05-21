import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IRevenueForecast, NewRevenueForecast } from '../revenue-forecast.model';

export type PartialUpdateRevenueForecast = Partial<IRevenueForecast> & Pick<IRevenueForecast, 'id'>;

export type EntityResponseType = HttpResponse<IRevenueForecast>;
export type EntityArrayResponseType = HttpResponse<IRevenueForecast[]>;

@Injectable({ providedIn: 'root' })
export class RevenueForecastService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/revenue-forecasts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/revenue-forecasts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(revenueForecast: NewRevenueForecast): Observable<EntityResponseType> {
    return this.http.post<IRevenueForecast>(this.resourceUrl, revenueForecast, { observe: 'response' });
  }

  update(revenueForecast: IRevenueForecast): Observable<EntityResponseType> {
    return this.http.put<IRevenueForecast>(`${this.resourceUrl}/${this.getRevenueForecastIdentifier(revenueForecast)}`, revenueForecast, {
      observe: 'response',
    });
  }

  partialUpdate(revenueForecast: PartialUpdateRevenueForecast): Observable<EntityResponseType> {
    return this.http.patch<IRevenueForecast>(`${this.resourceUrl}/${this.getRevenueForecastIdentifier(revenueForecast)}`, revenueForecast, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IRevenueForecast>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRevenueForecast[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRevenueForecast[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getRevenueForecastIdentifier(revenueForecast: Pick<IRevenueForecast, 'id'>): string {
    return revenueForecast.id;
  }

  compareRevenueForecast(o1: Pick<IRevenueForecast, 'id'> | null, o2: Pick<IRevenueForecast, 'id'> | null): boolean {
    return o1 && o2 ? this.getRevenueForecastIdentifier(o1) === this.getRevenueForecastIdentifier(o2) : o1 === o2;
  }

  addRevenueForecastToCollectionIfMissing<Type extends Pick<IRevenueForecast, 'id'>>(
    revenueForecastCollection: Type[],
    ...revenueForecastsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const revenueForecasts: Type[] = revenueForecastsToCheck.filter(isPresent);
    if (revenueForecasts.length > 0) {
      const revenueForecastCollectionIdentifiers = revenueForecastCollection.map(
        revenueForecastItem => this.getRevenueForecastIdentifier(revenueForecastItem)!
      );
      const revenueForecastsToAdd = revenueForecasts.filter(revenueForecastItem => {
        const revenueForecastIdentifier = this.getRevenueForecastIdentifier(revenueForecastItem);
        if (revenueForecastCollectionIdentifiers.includes(revenueForecastIdentifier)) {
          return false;
        }
        revenueForecastCollectionIdentifiers.push(revenueForecastIdentifier);
        return true;
      });
      return [...revenueForecastsToAdd, ...revenueForecastCollection];
    }
    return revenueForecastCollection;
  }
}
