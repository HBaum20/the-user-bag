import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import ManageUser from "./index";
import '@testing-library/jest-dom/extend-expect';
import {UserModel} from "../../../../../models";

describe('Manage User', () => {
   const user: UserModel = {
       id: '1',
       name: 'Arthur Morgan',
       balance: 8000.0,
       email: 'arthur.morgan@skybettingandgaming.com',
       phone: '07714 282896',
       password: 'hashedPassword'
   };

   const onBack = jest.fn();

   test('Should render all user fields and are disabled', () => {
       const { getByLabelText } = render(<ManageUser user={user} onBack={onBack}/>);

       const idField = getByLabelText('ID');
       const nameField = getByLabelText('Name');
       const balanceField = getByLabelText('Balance');
       const emailField = getByLabelText('Email Address');
       const phoneField = getByLabelText('Phone Number');
       const passwordField = getByLabelText('Hashed Password');

       expect(idField).toBeDisabled();
       expect(nameField).toBeDisabled();
       expect(balanceField).toBeDisabled();
       expect(emailField).toBeDisabled();
       expect(phoneField).toBeDisabled();
       expect(passwordField).toBeDisabled();
   });

   test('Should execute onBack when back button is clicked', () => {
       const { getByTestId } = render(<ManageUser user={user} onBack={onBack} />);

       const button = getByTestId('manage-user-back-button');
       fireEvent.click(button);

       expect(onBack).toHaveBeenCalledTimes(1);
   });
});