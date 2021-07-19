## Authorization API


### API

This module is where the API interface is implemented and connected the other two modules

### Data

This module is where the stateful Mongo data is implemented.

### Domain

This module represents the domain we will be working with. The domain module presents classes for the power of attorney
model that contains a Read or Write authorization for a Payment or Savings account.

## The task at hand

The following business requirements have been implemented:

- Users must be able to create write or read access for payments and savings accounts
- Users need to be able to retrieve a list of accounts they have read or write access for

## Endpoints:

The application was secured using Spring Security & stateteless JWT authentication. The endpoints starting with 
'/authorization' require an Authorization header that contains 'Bearer <JWT token>'.

- '/createUser' - unsecured, used for creating a User account, requiring only a name. The name must not already exist
- '/login' - unsecured, requires username and pin, returns 401 if the credentials are invalid, or 200 with a JWT token
  that must be used for all the secured endpoints
- '/addPowerOfAttorney' - unsecured (because I wanted to not change the domain object too much. Would have been better
  if this endpoint was secured, therefore we could retrieve the account name from the JWT token), the steps are:
      - account entity required is retrieved from the database 
      - a validation takes place between grantorName and accountHolderName
      - if the account does not have any granteeUsers, we add one
      - if it does, we check if the grantee already has a READ/WRITE role (so they can have both, but so that we do not create 2)
      - as an extra step, if the granteeUser doesn't have a User within our system, we create it and then return it

- '/authorized/createAccount' - secured, a validation takes place (between JWT token owner and accountHolderName), 
  and then an account is created
- '/authorized/logout' - the Login entity is updated within the database to be invalid
- '/authorized/getAllAccounts' - we retrieve all the accounts, and then we search within all granteeUsers

- The code is ready to go to production on delivery - no Unit/Integration tests because of the time limit

## Background information

A Power of Attorney is used when someone (grantor) wants to give access to his/her account to someone else (grantee). This
could be read access or write access. In this way the grantee can read/write in the grantors account.
Notice that this is a simplified version of reality.