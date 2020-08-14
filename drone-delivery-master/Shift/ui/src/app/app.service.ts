import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable, of, throwError} from "rxjs";
import {catchError, mergeMap} from "rxjs/operators";

@Injectable({providedIn: 'root'})
export class AppService {

  constructor(private http: HttpClient) {
  }

  /**
   * Gets Bins from the server
   * @param {object=} query What to query the server for
   *     (can be string, object or undefined)
   * @returns {promise} Signals the completion/rejection of the request
   *     If successful, the promise returns a list of Bins
   */
  getStringLength(str): Observable<any> {
    var url = window._SERVER_ENDPOINT + 'length';

    return this.http.post(url, {str: str})
      .pipe(catchError(err => {
        return throwError('Error retrieving String length.')
      }))
      .pipe(mergeMap(response => {
        if (!response) {
          return throwError('no data');
        } else {
          return of(response);
        }
      }))
  };

  getServerPaths(str): Observable<any> {
    var url = window._SERVER_ENDPOINT + 'paths';

    return this.http.post(url, {possiblePath: str})
      .pipe(catchError(err => {
        return throwError('Error retrieving Paths.')
      }))
      .pipe(mergeMap(response => {
        if (!response) {
          return throwError('no data');
        } else {
          return of(response);
        }
      }))
  };

}
