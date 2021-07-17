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

Boundaries

- You can add dependencies as you like
- You can design the data and API models as you like (what a dream, isn't it?)

Notes

- The code is ready to go to production on delivery

## Background information

A Power of Attorney is used when someone (grantor) wants to give access to his/her account to someone else (grantee). This
could be read access or write access. In this way the grantee can read/write in the grantors account.
Notice that this is a simplified version of reality.