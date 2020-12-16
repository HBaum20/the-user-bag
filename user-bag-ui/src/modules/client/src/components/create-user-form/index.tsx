import React, {ChangeEvent, useState} from 'react';
import {Button, FormGroup, InputGroup} from "@blueprintjs/core";
import AppToaster from "../../tools/toaster";
import axios from 'axios';
import {UserModel} from "../../../../../models";

interface CreateUserFormProps {
    onBack: () => void;
    onSuccess: () => void;
}

const CreateUserForm : React.FC<CreateUserFormProps> = ({onBack, onSuccess}) => {

    const [id, setId] = useState<string | null>(null)
    const [name, setName] = useState<string | null>(null)
    const [balance, setBalance] = useState<number | null>(null)
    const [email, setEmail] = useState<string | null>(null)
    const [phone, setPhone] = useState<string | null>(null)
    const [password, setPassword] = useState<string | null>(null)
    const [confirmPassword, setConfirmPassword] = useState<string | null>(null)

    const [alreadySubmitted, setAlreadySubmitted] = useState(false);

    const setField = (event: ChangeEvent<HTMLInputElement>, setter: Function): void => setter(event.target.value)

    const allFieldsPopulated = (): boolean =>
        !!((id) && (name) && (balance) && (email)
            && (phone) && (password) && (confirmPassword))

    const createUserModelFromState = (): UserModel => {
        if(!allFieldsPopulated()) {
            throw "Form Incomplete";
        } else if(password !== confirmPassword) {
            throw "password and confirm password don't match";
        } else {
            return {id: id!!, name: name!!, balance: balance!!, email: email!!, phone: phone!!, password: password!!};
        }
    }

    const postToApi = () => {
        setAlreadySubmitted(true);
        try {
            const model = createUserModelFromState();
            axios.post('/users', model)
                .then(() => {
                    AppToaster.show({message: `Account with ID ${model.id} successfully created!`, intent: 'success'});
                    onSuccess();
                })
                .catch((error) => AppToaster.show({message: `Error creating account: ${error}`, intent: 'danger'}))
        } catch(err) {
            setAlreadySubmitted(false);
            AppToaster.show({message: err, intent: "danger"})
        }
    }

    return (
        <div>
            <FormGroup>
                <label htmlFor="id">ID </label>
                <InputGroup
                    id="id"
                    name="id"
                    type="text"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setId)}
                />
                <label htmlFor="name">Name </label>
                <InputGroup
                    id="name"
                    name="name"
                    type="text"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setName)}
                />
                <label htmlFor="deposit">Initial Deposit </label>
                <InputGroup
                    id="deposit"
                    name="deposit"
                    type="number"
                    large
                    min={0}
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setBalance)}
                />
                <label htmlFor="email">Email Address </label>
                <InputGroup
                    id="email"
                    name="email"
                    type="text"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setEmail)}
                />
                <label htmlFor="phone">Phone Number </label>
                <InputGroup
                    id="phone"
                    name="phone"
                    type="text"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setPhone)}
                />
                <label htmlFor="password">Password </label>
                <InputGroup
                    id="password"
                    name="password"
                    type="password"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setPassword)}
                />
                <label htmlFor="confirmPassword">Confirm Password</label>
                <InputGroup
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setConfirmPassword)}
                />
            </FormGroup>
            <div>
                <Button
                    data-testid="create-user-submit-button"
                    onClick={postToApi}
                    disabled={!allFieldsPopulated() || alreadySubmitted}
                >Submit</Button>
                <Button onClick={onBack}>Back</Button>
            </div>
        </div>
    );
}

export default CreateUserForm;