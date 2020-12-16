import React, {ChangeEvent, useState} from 'react';
import {Button, FormGroup, InputGroup} from "@blueprintjs/core";

interface AuthDialogProps {
    onSubmit: (id: string, password: string) => void
}

const AuthDialog: React.FC<AuthDialogProps> = ({onSubmit}) => {
    const [id, setId] = useState<string | null>(null);
    const [password, setPassword] = useState<string | null>(null);

    const setField = (event: ChangeEvent<HTMLInputElement>, setter: Function): void => setter(event.target.value)

    const notPopulated = (): boolean => {
        return ((id == null) || (password == null));
    }

    return (
        <div>
            <FormGroup>
                <label htmlFor="id">ID</label>
                <InputGroup
                    id="id"
                    name="id"
                    type="text"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setId)} />
                <label htmlFor="password">Password</label>
                <InputGroup
                    id="password"
                    name="password"
                    type="password"
                    large
                    onChange={(event: ChangeEvent<HTMLInputElement>) => setField(event, setPassword)} />
            </FormGroup>
            <Button onClick={() => onSubmit(id!!, password!!)} large disabled={notPopulated()} data-testid="auth-submit-button">Submit</Button>
        </div>
    );
};

export default AuthDialog;