package alex.com.myplaces.domain.usecases;

public abstract class UseCase<T extends UseCase.RequestValues, U extends UseCase.ResponseValues> {

    public interface UseCaseResponseCallback<U> {

        void response(U responseValues);

        void responseError();
    }

    public abstract void execute(T requestValues, UseCaseResponseCallback<U> responseCallback);

    public interface RequestValues {

    }

    public interface ResponseValues {

    }

}
